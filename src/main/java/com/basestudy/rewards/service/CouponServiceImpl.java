package com.basestudy.rewards.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponStatus;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.entity.Coupon;
import com.basestudy.rewards.repository.CouponRepository;
import com.basestudy.rewards.service.mapper.CouponMapper;

import jakarta.persistence.NoResultException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;

    @Transactional()
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto){
        valid(couponDto);
        couponDto.setIssuedQuantity(0);
        couponDto.setStatus(CouponStatus.ACTIVE);
        couponRepository.save(CouponMapper.toEntity(couponDto));
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }


    @Transactional(readOnly = true)
    public ApiResponseWrapper<CouponDto> getCouponInfo(long id){
        // TODO: 이해해보기
        // return couponRepository.findById(id).map(CouponMapper::toDto)
        // .map(ApiResponseWrapper::createSuccess)
        // .orElse(ApiResponseWrapper.createFail(null, "400", "조회된 결과가 없습니다."));
        return couponRepository.findById(id)
                            .map(coupon -> ApiResponseWrapper.createSuccess(CouponMapper.toDto(coupon)))
                            .orElse(ApiResponseWrapper.createFail(null, "400", "조회된 결과가 없습니다."));
    }

    @Transactional
    public ApiResponseWrapper<?> getCoupons(){
        //TODO: 페이징처리하기
        return ApiResponseWrapper.createSuccess(null);
    }

    @Transactional
    public ApiResponseWrapper<?> updateCoupon(CouponDto couponDto){
        Coupon coupon = this.getCoupon(couponDto.getCouponId());
        if(this.canIssueCoupon(coupon)){
            throw new IllegalArgumentException("발급중인 쿠폰입니다. 발급중단을 이용하세요.");
        }
        if(coupon.getStatus().equals(CouponStatus.ACTIVE)){
            throw new IllegalArgumentException("활성화된 상태의 쿠폰 정보만 수정 가능합니다.");
        }
        valid(couponDto);
        coupon.setAvailableFrom(couponDto.getAvailableFrom());
        coupon.setAvailableTo(couponDto.getAvailableTo());
        coupon.setTotalQuantity(couponDto.getTotalQuantity());
        coupon.setUseDays(couponDto.getUseDays());

        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Transactional
    public ApiResponseWrapper<?> suspendCoupon(long id, String suspensionReason){
        Coupon coupon = this.getCoupon(id);
        if(coupon.getStatus() != CouponStatus.ACTIVE){
            throw new IllegalArgumentException("활성상태의 쿠폰이 아닙니다.");
        }
        if(suspensionReason.isBlank()){
            throw new IllegalArgumentException("발급 중단 사유를 입력해주세요");
        }
        coupon.setStatus(CouponStatus.SUSPENDED);
        coupon.setSuspensionReason(suspensionReason);
        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Transactional
    public ApiResponseWrapper<?> deleteCoupon(long id){
        //TODO: 확인창 다시보내서 확인받는거 구현가능한가 future로 대기타고 흠.. 
        Coupon coupon = this.getCoupon(id);
        if(this.canIssueCoupon(coupon)){
                throw new IllegalArgumentException("발급중인 쿠폰입니다. 발급중단을 이용하세요.");
        }
        coupon.setStatus(CouponStatus.DELETED);
        couponRepository.save(coupon);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }

    @Transactional(readOnly = true)
    protected Coupon getCoupon(long id){
        return couponRepository.findById(id)
                        .orElseThrow(()->new NoResultException("존재하지 않습니다."));
    }

    protected void checkCouponExhaustion(Coupon coupon) {
        if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            coupon.setStatus(CouponStatus.EXHAUSTED);
        }
    }

    protected void quantityValid(CouponDto couponDto){
        if(couponDto.getTotalQuantity() <= couponDto.getIssuedQuantity()){
            //이거에 대해서는 발행할때 다시 확인
            throw new IllegalArgumentException("발급수량이 총 수량을 넘었습니다");
        }
    }

    protected boolean canIssueCoupon(Coupon coupon) {
        if (coupon.getStatus().equals(CouponStatus.ACTIVE)) {
            LocalDateTime now = LocalDateTime.now();
            return now.isAfter(coupon.getAvailableFrom()) && now.isBefore(coupon.getAvailableTo());
        }
        return false; // ACTIVE가 아니면 발급 불가
    }

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
}
