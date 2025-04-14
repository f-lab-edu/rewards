package com.basestudy.rewards.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponLockStatus;
import com.basestudy.rewards.controller.dto.CouponLock;
import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.entity.Coupon;
import com.basestudy.rewards.entity.Member;
import com.basestudy.rewards.repository.UserCouponRepository;
import com.basestudy.rewards.service.mapper.UserCouponMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Setter
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService{
    private final UserCouponRepository userCouponRepository;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;
    private final CouponService couponService;

    @Value("${spring.redis.ttl}")
    private int ttl;
    @Value("${spring.redis.coupon.lock.prefix}")
    private String couponLockKey;
    
    public ApiResponseWrapper<?> distributeCoupon(Member member, long couponId){
        //TODO: 권한확인
        String key = couponLockKey + member.getId();
        CouponLock couponLock = (CouponLock)redisService.get(key); //coupon:lock:memberId
        
        if(couponLock == null){
            return handleCouponLockNotFound(key, member.getId(), couponId);
        }
        return handleExistingCouponLock(couponLock);  
    }

    @Transactional
    public void saveUserCoupon(UserCouponDto userCouponDto) {
        //발급db저장
        Coupon coupon = couponService.findCouponById(userCouponDto.getCouponId());
        userCouponDto.setIssueDate(LocalDateTime.now());
        userCouponDto.setExpiredDate(LocalDateTime.now().plusDays(coupon.getUseDays()));
        
        userCouponRepository.save(UserCouponMapper.toEntity(userCouponDto));

        String key = couponLockKey + userCouponDto.getMemberId();
        redisService.save(key, CouponLock.builder()
                .couponId(userCouponDto.getCouponId())
                .status(CouponLockStatus.DONE)
                .build());
    }

    private ApiResponseWrapper<?> handleCouponLockNotFound(String key, Long memberId, Long couponId) {
        // 쿠폰 발급 여부 확인
        return userCouponRepository.findByMemberIdAndCouponId(memberId, couponId)
                .map(userCoupon -> ApiResponseWrapper.createSuccess("이미 쿠폰을 발급받았습니다"))
                .orElseGet(() -> {
                    // 쿠폰 수량확인
                    couponService.decreaseCouponQuantity(couponId);
                    // 쿠폰 Lock 저장 및 Kafka 호출
                    saveCouponLockAndTriggerKafka(key, couponId, memberId);
                    return ApiResponseWrapper.createSuccess("발급되었습니다.");
                });
    }

    private void saveCouponLockAndTriggerKafka(String key, Long couponId, long memberId) {
        // 1. 쿠폰 Lock 저장
        redisService.save(key, CouponLock.builder()
                .couponId(couponId)
                .status(CouponLockStatus.PROCESSING)
                .build());
        try {
            // 2. Kafka 호출
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(UserCouponMapper.toKafkaObj(memberId, couponId));
            triggerKafkaEvent(message);
        } catch (Exception e) {
            // 예외 발생 시 Lock 삭제
            redisService.delete(key);
            throw new RuntimeException("Kafka 호출 중 오류가 발생했습니다.", e);
        }
    }
    
    private ApiResponseWrapper<?> handleExistingCouponLock(CouponLock couponLock) {
        if (couponLock.getStatus().equals(CouponLockStatus.DONE)) {
            return ApiResponseWrapper.createSuccess("이미 쿠폰을 발급받았습니다");
        }
        return ApiResponseWrapper.createSuccess("처리중입니다. 잠시후 다시 시도해주세요.");
    }

    private void triggerKafkaEvent(String message) {
        kafkaProducer.sendMessage(message);
    }
}
