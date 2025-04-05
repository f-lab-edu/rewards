package com.basestudy.rewards.controller.dto;

import java.time.LocalDateTime;

import com.basestudy.rewards.entity.Coupon;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class CouponDto {
    private long couponId;
    private String code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime availableTo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime availableFrom;
    private int totalAmount;
    private int useDays;

    public CouponDto(){}
    public CouponDto(long couponId, String code, LocalDateTime availableTo, LocalDateTime availableFrom, int totalAmount, int useDays){
        this.couponId = couponId;
        this.code = code;
        this.availableTo = availableTo;
        this.availableFrom = availableFrom;
        this.totalAmount = totalAmount;
        this.useDays = useDays;
    }
    public CouponDto(String code, LocalDateTime availableTo, LocalDateTime availableFrom, int totalAmount, int useDays){
        this.code = code;
        this.availableTo = availableTo;
        this.availableFrom = availableFrom;
        this.totalAmount = totalAmount;
        this.useDays = useDays;
    }
    public Coupon toEntity(){
        return Coupon.builder()
                    .code(this.code)
                    .availableTo(this.availableTo)
                    .availableFrom(this.availableFrom)
                    .totalAmount(this.totalAmount)
                    .useDays(this.useDays)
                    .build();
    }
}
