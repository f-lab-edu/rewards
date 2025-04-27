package com.basestudy.rewards.controller.dto;

import com.basestudy.rewards.constants.CouponLockStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@Builder
public class CouponLock {
    private Long couponId;
    private CouponLockStatus status;
}
