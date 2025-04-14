package com.basestudy.rewards.controller.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponDto {
    private long id;
    private long couponId;
    private long memberId;
    private LocalDateTime issueDate;
    private LocalDateTime expiredDate;
}
