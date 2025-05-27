package com.basestudy.rewards.controller.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UserWithCouponsDto {
    private String memberId;
    private String memberName;
    private List<CouponDto> coupons;

    public UserWithCouponsDto(String memberId, String memberName, List<CouponDto> coupons) {
        this.memberId = memberId;
        this.coupons = coupons;
    }
}
