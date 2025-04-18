package com.basestudy.rewards.service.mapper;

import com.basestudy.rewards.controller.dto.UserCouponDto;
import com.basestudy.rewards.domain.UserCoupon;

public class UserCouponMapper {
    public static UserCouponDto toKafkaObj(long memberId, long couponId){
        return UserCouponDto.builder().memberId(memberId).couponId(couponId).build();
    }
    public static UserCouponDto toDto(UserCoupon userCoupon){
        return UserCouponDto.builder()
                    .id(userCoupon.getId())
                    .couponId(userCoupon.getCouponId())
                    .memberId(userCoupon.getMemberId())
                    .issueDate(userCoupon.getIssueDate())
                    .expiredDate(userCoupon.getExpiredDate())
                    .build();
    }

    public static UserCoupon toEntity(UserCouponDto userCouponDto){
        return UserCoupon.builder()
                    .id(userCouponDto.getId())
                    .couponId(userCouponDto.getCouponId())
                    .memberId(userCouponDto.getMemberId())
                    .issueDate(userCouponDto.getIssueDate())
                    .expiredDate(userCouponDto.getExpiredDate())
                    .build();
    }
}
