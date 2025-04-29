package com.basestudy.rewards.service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.domain.Member;

public interface UserCouponService {
    public ApiResponseWrapper<?> distributeCoupon(Member member, Long couponId);
    public void saveUserCoupon(Long couponId, Long userId);
}
