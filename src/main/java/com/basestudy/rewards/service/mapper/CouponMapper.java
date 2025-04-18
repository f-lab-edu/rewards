package com.basestudy.rewards.service.mapper;

import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.domain.Coupon;
import com.basestudy.rewards.domain.Quantity;

public class CouponMapper {
    public static CouponDto toDto(Coupon coupon){
        return CouponDto.builder()
                    .couponId(coupon.getId())
                    .name(coupon.getName())
                    .availableFrom(coupon.getAvailableFrom())
                    .availableTo(coupon.getAvailableTo())
                    .totalQuantity(coupon.getQuantity().getTotal())
                    .issuedQuantity(coupon.getQuantity().getIssued())
                    .useDays(coupon.getUseDays())
                    .status(coupon.getStatus())
                    .suspensionReason(coupon.getSuspensionReason())
                    .build();
    }

    public static Coupon toEntity(CouponDto couponDto){
        Quantity quantity = new Quantity(
            couponDto.getTotalQuantity(),
            couponDto.getIssuedQuantity()
        );
        return Coupon.builder()
            .id(couponDto.getCouponId())
            .name(couponDto.getName())
            .availableFrom(couponDto.getAvailableFrom())
            .availableTo(couponDto.getAvailableTo())
            .quantity(quantity)
            .useDays(couponDto.getUseDays())
            .status(couponDto.getStatus())
            .suspensionReason(couponDto.getSuspensionReason())
            .build();
    }
}
