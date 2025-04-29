package com.basestudy.rewards.controller.dto;

import com.basestudy.rewards.constants.CouponLockStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor  // 기본 생성자 생성
@AllArgsConstructor
public class CouponLock {
    private Long couponId;
    private CouponLockStatus status;
}
