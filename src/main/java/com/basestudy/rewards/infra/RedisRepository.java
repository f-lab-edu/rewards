package com.basestudy.rewards.infra;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.basestudy.rewards.constants.CouponLockStatus;
import com.basestudy.rewards.constants.CouponStatus;
import com.basestudy.rewards.controller.dto.CouponLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COUPON_COUNT_PREFIX = "coupon:count:";
    private static final String COUPON_EXHAUSTED_CHANNEL = "coupon-exhausted-channel";
    @Value("${spring.data.redis.ttl}")
    private int ttlInSeconds;
    @Value("${spring.data.redis.coupon.lock.prefix}")
    private String COUPON_LOCK_PREFIX;

    // coupon:lock:{memberId}
    public CouponLock getLockKey(Long memberId) {
        if(redisTemplate.opsForValue().get(userLockKey(memberId))==null){
            return null;
        }else{
            return (CouponLock) redisTemplate.opsForValue().get(userLockKey(memberId));
        }        
    }

    public void saveLockKeyDone(Long memberId, Long couponId) {
        redisTemplate.opsForValue().set(
            userLockKey(memberId), CouponLock.builder()
                .couponId(couponId)
                .status(CouponLockStatus.DONE)
                .build(), ttlInSeconds, TimeUnit.SECONDS);
    }

    public void saveLockKeyProcessing(Long memberId, Long couponId) {
        redisTemplate.opsForValue().set(
            userLockKey(memberId), CouponLock.builder()
                .couponId(couponId)
                .status(CouponLockStatus.PROCESSING)
                .build(), ttlInSeconds, TimeUnit.SECONDS);
    }

    public void deleteLockKey(Long memberId) {
        redisTemplate.delete(userLockKey(memberId));
    }

    // coupon:count:{couponId}
    // 데이터 저장 (TTL 포함)
    public void saveCountKey(Long couponId, int quantity, long ttlInSeconds) {
        redisTemplate.opsForValue().set(countKey(couponId), quantity, ttlInSeconds, TimeUnit.SECONDS);
    }

    // 데이터 저장 (TTL 없이)
    public void saveCountKey(Long couponId, int quantity) {
        redisTemplate.opsForValue().set(countKey(couponId), quantity);
    }

    public void saveExhausted(Long couponId, CouponStatus status) {
        redisTemplate.opsForValue().set(countKey(couponId), status);
    }

    // 데이터 삭제
    public void deleteCountKey(Long couponId) {
        redisTemplate.delete(countKey(couponId));
    }

    // TTL 확인
    public long getTTLCountKey(Long couponId) {
        return redisTemplate.getExpire(countKey(couponId), TimeUnit.SECONDS);
    }

    // 수량감소
    public long decrement(Long couponId) {
        return redisTemplate.opsForValue().decrement(countKey(couponId));
    }
    
    // 수량증가
    public long increment(Long couponId) {
        return redisTemplate.opsForValue().increment(countKey(couponId));
    }
    
    // pub
    public void sendMessageExhausted(Object message){
        redisTemplate.convertAndSend(COUPON_EXHAUSTED_CHANNEL, message);
        log.info("Message sent to channel={}, message={}", COUPON_EXHAUSTED_CHANNEL, message);
    }

    private String countKey(Long couponId) {
        return COUPON_COUNT_PREFIX + couponId;
    }
    
    private String userLockKey(Long memberId) {
        return COUPON_LOCK_PREFIX + memberId;
    }

}
