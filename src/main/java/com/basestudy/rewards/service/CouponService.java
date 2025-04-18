package com.basestudy.rewards.service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.domain.Coupon;

public interface CouponService {
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto);
    public ApiResponseWrapper<CouponDto> getCoupon(long id);
    public ApiResponseWrapper<?> getCoupons();
    public ApiResponseWrapper<?> updateCoupon(CouponDto couponDto);
    public ApiResponseWrapper<?> suspendCoupon(long id, String suspensionReason);
    public ApiResponseWrapper<?> deleteCoupon(long id);
    public Coupon findCouponById(long id);
    public Long decreaseCouponQuantity(long couponId);
    public ApiResponseWrapper<?> setInitialCouponQuantity(long couponId);
    public void setExhaustionCoupon(long couponId);
}
