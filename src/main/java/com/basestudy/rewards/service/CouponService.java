package com.basestudy.rewards.service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;

public interface CouponService {
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto);
    
}
