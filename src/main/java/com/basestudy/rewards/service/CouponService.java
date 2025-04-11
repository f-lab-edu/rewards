package com.basestudy.rewards.service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;

public interface CouponService {
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto);
    public ApiResponseWrapper<CouponDto> getCouponInfo(long id);
    public ApiResponseWrapper<?> getCoupons();
    public ApiResponseWrapper<?> updateCoupon(CouponDto couponDto);
    public ApiResponseWrapper<?> suspendCoupon(long id, String suspensionReason);
    public ApiResponseWrapper<?> deleteCoupon(long id);
}
