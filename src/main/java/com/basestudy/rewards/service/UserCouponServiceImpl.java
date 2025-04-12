package com.basestudy.rewards.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.constants.CouponLockStatus;
import com.basestudy.rewards.controller.dto.CouponLock;
import com.basestudy.rewards.entity.Member;
import com.basestudy.rewards.repository.UserCouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCouponRepository userCouponRepository;

    @Value("${spring.redis.ttl}")
    private int ttl;
    @Value("${spring.redis.coupon.lock.prefix}")
    private String couponLockKey;

    public ApiResponseWrapper<?> distributeCoupon(Member member, long couponId){
        //TODO: 권한확인
        String key = couponLockKey + member.getId();
        CouponLock couponLock = (CouponLock)getValue(key);
       
        if(couponLock == null){
            return handleCouponLockNotFound(key, member.getId(), couponId);
        }
        return handleExistingCouponLock(couponLock);  
    }

    private ApiResponseWrapper<?> handleCouponLockNotFound(String key, Long memberId, Long couponId) {
        // 쿠폰 발급 여부 확인
        return userCouponRepository.findByMemberIdAndCouponId(memberId, couponId)
                .map(userCoupon -> ApiResponseWrapper.createSuccess("이미 쿠폰을 발급받았습니다"))
                .orElseGet(() -> {
                    // 쿠폰 Lock 저장 및 Kafka 호출
                    saveCouponLockAndTriggerKafka(key, couponId);
                    return ApiResponseWrapper.createSuccess("발급되었습니다.");
                });
    }

    private void saveCouponLockAndTriggerKafka(String key, Long couponId) {
        // 1. 쿠폰 Lock 저장
        saveKey(key, CouponLock.builder()
                .couponId(couponId)
                .status(CouponLockStatus.PROCESSING)
                .build());
    
        try {
            // 2. Kafka 호출
            triggerKafkaEvent(couponId);
        } catch (Exception e) {
            // 예외 발생 시 Lock 삭제
            deleteKey(key);
            throw new RuntimeException("Kafka 호출 중 오류가 발생했습니다.", e);
        }
    }
    
    private ApiResponseWrapper<?> handleExistingCouponLock(CouponLock couponLock) {
        if (couponLock.getStatus().equals(CouponLockStatus.DONE)) {
            return ApiResponseWrapper.createSuccess("이미 쿠폰을 발급받았습니다");
        }
        return ApiResponseWrapper.createSuccess("처리중입니다. 잠시후 다시 시도해주세요.");
    }
    
    private void triggerKafkaEvent(Long couponId) {
        // Kafka 호출 로직 구현
    }

    private Object getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }
    
    private void saveKey(String key, Object obj){
        redisTemplate.opsForValue().set(key, obj, ttl);
    }

    private void deleteKey(String key){
        redisTemplate.delete(key);
    }
}
