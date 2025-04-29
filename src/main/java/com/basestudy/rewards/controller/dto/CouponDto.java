package com.basestudy.rewards.controller.dto;

import java.time.LocalDateTime;

import com.basestudy.rewards.constants.CouponStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "쿠폰")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor  // 기본 생성자 생성
@AllArgsConstructor
public class CouponDto {
    @Schema(description = "쿠폰ID")
    private Long couponId;
    @Schema(description = "쿠폰이름", nullable = false)
    private String name;
    @Schema(description = "발급시작일", example = "yyyy-MM-dd HH:mm:ss", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime availableFrom;
    @Schema(description = "발급종료일", example = "yyyy-MM-dd HH:mm:ss", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime availableTo;
    @Schema(description = "발급총량", nullable = false, type = "integer")
    private int totalQuantity;
    @Schema(description = "발급수량", nullable = false, type = "integer")
    private int issuedQuantity;
    @Schema(description = "사용가능일수", nullable = false, type = "integer")
    private int useDays;
    @Schema(description = "쿠폰상태", nullable = false, type = "string")
    private CouponStatus status;
    @Schema(description = "발급중단사유", type = "string")
    private String suspensionReason;
}
