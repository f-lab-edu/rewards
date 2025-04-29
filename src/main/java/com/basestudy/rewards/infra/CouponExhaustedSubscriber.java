package com.basestudy.rewards.infra;

import com.basestudy.rewards.service.CouponService;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CouponExhaustedSubscriber implements MessageListener{
    private final CouponService couponService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Long couponId = Long.valueOf(new String(message.getBody()));

        // MySQL에서 쿠폰 상태를 "소진"으로 업데이트
        couponService.updateCouponStatusToExhausted(couponId);
    }
}
