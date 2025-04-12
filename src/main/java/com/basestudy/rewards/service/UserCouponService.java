package com.basestudy.rewards.service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.entity.Member;

public interface UserCouponService {
    public ApiResponseWrapper<?> distributeCoupon(Member member, long couponId);
}
