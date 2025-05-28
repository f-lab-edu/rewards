package com.basestudy.rewards.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
public class UserWithCouponsDto {
    private Long memberId;
    private String memberName;
    private Long couponId;
    private String couponName;
    private LocalDateTime issueDate;
    private LocalDateTime expiredDate;
}
