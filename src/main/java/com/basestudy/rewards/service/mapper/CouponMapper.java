package com.basestudy.rewards.service.mapper;

import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.entity.Coupon;

public class CouponMapper {
    public static CouponDto toDto(Coupon coupon){
        return CouponDto.builder()
                    .couponId(coupon.getId())
                    .name(coupon.getName())
                    .availableFrom(coupon.getAvailableFrom())
                    .availableTo(coupon.getAvailableTo())
                    .totalQuantity(coupon.getTotalQuantity())
                    .issuedQuantity(coupon.getIssuedQuantity())
                    .useDays(coupon.getUseDays())
                    .status(coupon.getStatus())
                    .suspensionReason(coupon.getSuspensionReason())
                    .build();
    }

    public static Coupon toEntity(CouponDto couponDto){
        return Coupon.builder()
            .id(couponDto.getCouponId())
            .name(couponDto.getName())
            .availableFrom(couponDto.getAvailableFrom())
            .availableTo(couponDto.getAvailableTo())
            .totalQuantity(couponDto.getTotalQuantity())
            .issuedQuantity(couponDto.getIssuedQuantity())
            .useDays(couponDto.getUseDays())
            .status(couponDto.getStatus())
            .suspensionReason(couponDto.getSuspensionReason())
            .build();
    }
}
