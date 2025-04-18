package com.basestudy.rewards.infra;

import java.util.concurrent.TimeUnit;

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
    private static final String COUPON_EXHAUSTED = "coupon-exhausted";
    private static final int ttlInSeconds = 60;
    private static final String COUPON_LOCK_PREFIX = "coupon:lock:";

    // coupon:lock:{memberId}
    public CouponLock getLockKey(long memberId) {
        return (CouponLock)redisTemplate.opsForValue().get(lockKey(memberId));
    }

    public void saveLockKeyDone(long memberId, long couponId) {
        redisTemplate.opsForValue().set(
            lockKey(memberId), CouponLock.builder()
                .couponId(couponId)
                .status(CouponLockStatus.DONE)
                .build(), ttlInSeconds, TimeUnit.SECONDS);
    }

    public void saveLockKeyProcessing(long memberId, long couponId) {
        redisTemplate.opsForValue().set(
            lockKey(memberId), CouponLock.builder()
                .couponId(couponId)
                .status(CouponLockStatus.PROCESSING)
                .build(), ttlInSeconds, TimeUnit.SECONDS);
    }

    public void deleteLockKey(long memberId) {
        redisTemplate.delete(lockKey(memberId));
    }

    // coupon:count:{couponId}
    // 데이터 저장 (TTL 포함)
    public void saveCountKey(long couponId, int quantity, long ttlInSeconds) {
        redisTemplate.opsForValue().set(countKey(couponId), quantity, ttlInSeconds, TimeUnit.SECONDS);
    }

    // 데이터 저장 (TTL 없이)
    public void saveCountKey(long couponId, int quantity) {
        redisTemplate.opsForValue().set(countKey(couponId), quantity);
    }

    public void saveExhausted(long couponId, CouponStatus status) {
        redisTemplate.opsForValue().set(countKey(couponId), status);
    }

    // 데이터 삭제
    public void deleteCountKey(long couponId) {
        redisTemplate.delete(countKey(couponId));
    }

    // TTL 확인
    public Long getTTLCountKey(long couponId) {
        return redisTemplate.getExpire(countKey(couponId), TimeUnit.SECONDS);
    }

    // 수량감소
    public long decrement(long couponId) {
        return redisTemplate.opsForValue().decrement(countKey(couponId));
    }
    
    // pub
    public void sendMessageExhausted(Object message){
        redisTemplate.convertAndSend(COUPON_EXHAUSTED, message);
        log.info("Message sent to channel={}, message={}", COUPON_EXHAUSTED, message);
    }

    private String countKey(long couponId) {
        return COUPON_COUNT_PREFIX + couponId;
    }
    
    private String lockKey(long couponId) {
        return COUPON_LOCK_PREFIX + couponId;
    }

}
