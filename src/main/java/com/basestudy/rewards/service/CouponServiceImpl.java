package com.basestudy.rewards.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponStatus;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.domain.Coupon;
import com.basestudy.rewards.domain.Quantity;
import com.basestudy.rewards.infra.RedisRepository;
import com.basestudy.rewards.repository.CouponRepository;
import com.basestudy.rewards.service.mapper.CouponMapper;

import jakarta.persistence.NoResultException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;
    private final RedisRepository redisRepository;

    @Override
    @Transactional
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto){
        valid(couponDto);
        couponDto.setIssuedQuantity(0);
        couponDto.setStatus(CouponStatus.STANDBY);
        couponRepository.save(CouponMapper.toEntity(couponDto));
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseWrapper<CouponDto> getCoupon(Long id){
        return couponRepository.findById(id)
                            .map(coupon -> ApiResponseWrapper.createSuccess(CouponMapper.toDto(coupon)))
                            .orElse(ApiResponseWrapper.createFail(null, "400", "조회된 결과가 없습니다."));
    }

    @Override
    @Transactional
    public ApiResponseWrapper<?> getCoupons(){
        return ApiResponseWrapper.createSuccess(null);
    }

    @Override
    @Transactional
    public ApiResponseWrapper<?> updateCoupon(CouponDto couponDto){
        Coupon coupon = this.findCouponById(couponDto.getCouponId());
        coupon.update(couponDto.getAvailableFrom(), couponDto.getAvailableTo(), couponDto.getTotalQuantity(), couponDto.getUseDays());

        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Override
    @Transactional
    public ApiResponseWrapper<?> suspendCoupon(Long id, String suspensionReason){
        Coupon coupon = this.findCouponById(id);
        coupon.suspend(suspensionReason);

        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Override
    @Transactional
    public ApiResponseWrapper<?> deleteCoupon(Long id){
        Coupon coupon = this.findCouponById(id);
        coupon.delete();

        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon findCouponById(Long id){
        return couponRepository.findById(id)
                        .orElseThrow(()->new NoResultException("존재하지 않습니다."));
    }

    /*
     * 쿠폰 발행시 검증
     */
    private CouponDto valid(CouponDto couponDto){
        if(couponDto.getName().isBlank()){
            throw new ValidationException("쿠폰명을 입력해주세요");
        }
        if(couponDto.getAvailableFrom().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("발급시작일은 오늘날짜 이후값이여야 합니다.");
        }
        if(couponDto.getAvailableTo().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("발급종료일은 오늘날짜 이후값이여야 합니다.");
        }
        if(couponDto.getAvailableFrom().isAfter(couponDto.getAvailableTo())){
            throw new IllegalArgumentException("발급종료일은 발급시작일 이후여야합니다.");
        }
        if(couponDto.getTotalQuantity()==0){
            throw new IllegalArgumentException("총 발급 수량은 0보다 커야합니다.");
        }
        if(couponDto.getUseDays()==0){
            throw new IllegalArgumentException("사용가능 일수는 0보다 커야합니다.");
        }

        return couponDto;
    }

    /*
     * 쿠폰 활성화
     */
    @Override
    @Transactional
    public ApiResponseWrapper<?> setInitialCouponQuantity(Long couponId) {
        Coupon coupon = this.findCouponById(couponId);
        redisRepository.saveCountKey(couponId, coupon.getQuantity().getTotal(), coupon.getAvailableSeconds());

        coupon.activate();
        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("쿠폰이 활성화되었습니다.");
    }

    /*
     * 쿠폰 수량 차감
     * 소진시 redis 메세지 전송 및 예외처리
     */
    @Override
    public Long decreaseCouponQuantity(Long couponId) {
        Long remainingQuantity = redisRepository.decrement(couponId);
       
        if (remainingQuantity != null && remainingQuantity < 0) {  
            publishCouponSoldOutEvent(couponId);
            throw new RuntimeException("쿠폰이 모두 소진되었습니다.");
        }
        
        return remainingQuantity;
    }

    /*
     * redis 소진상태 pub
     * 여러번 받는것 허용
     */
    private void publishCouponSoldOutEvent(Long couponId) {
        redisRepository.sendMessageExhausted(couponId);
    }

    /*
     * redis sub에서 호출하는 
     * mysql 쿠폰 소진 처리
     * 반복 호출을 대비해 상태확인
     */
    @Override
    @Transactional
    public void updateCouponStatusToExhausted(Long couponId){
        Coupon coupon = this.findCouponById(couponId);
        Quantity quantity = coupon.getQuantity();
        if(!coupon.isExhausted()){
            quantity.setIssued(quantity.getTotal());
            coupon.setQuantity(quantity);
            coupon.setStatus(CouponStatus.EXHAUSTED);
            couponRepository.save(coupon);
            //redisRepository.saveExhausted(couponId, CouponStatus.EXHAUSTED);
        }
    }
}
