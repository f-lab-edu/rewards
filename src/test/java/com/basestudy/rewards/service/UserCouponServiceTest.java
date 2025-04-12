package com.basestudy.rewards.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.basestudy.rewards.controller.dto.CouponLock;
import com.basestudy.rewards.entity.UserCoupon;

@SpringBootTest
public class UserCouponServiceTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void save(){
        CouponLock couponLock = new CouponLock();
        couponLock.setCouponId(5L);
        couponLock.setStatus("dddd");
        couponLock.setTt(LocalDateTime.now());
        redisTemplate.opsForValue().set("coupon:lock:user1", couponLock);
        CouponLock c = (CouponLock)redisTemplate.opsForValue().get("coupon:lock:user");
        System.out.print(c.toString());
    }
    
}
