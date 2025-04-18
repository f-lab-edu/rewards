package com.basestudy.rewards.domain;

import java.time.LocalDateTime;

public class CouponPolicy {
        public static boolean canIssue(Coupon coupon, LocalDateTime now) {
        return coupon.isActive() && now.isAfter(coupon.getAvailableFrom()) && now.isBefore(coupon.getAvailableTo());
    }
}
