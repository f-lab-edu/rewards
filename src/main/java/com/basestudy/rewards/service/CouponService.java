package com.basestudy.rewards.service;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.domain.Coupon;

public interface CouponService {
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto);
    public ApiResponseWrapper<CouponDto> getCoupon(Long id);
    public ApiResponseWrapper<?> getCoupons();
    public ApiResponseWrapper<?> updateCoupon(CouponDto couponDto);
    public ApiResponseWrapper<?> suspendCoupon(Long id, String suspensionReason);
    public ApiResponseWrapper<?> deleteCoupon(Long id);
    public Coupon findCouponById(Long id);
    public Long decreaseCouponQuantity(Long couponId);
    public ApiResponseWrapper<?> setInitialCouponQuantity(Long couponId);
    public void updateCouponStatusToExhausted(Long couponId);
}
