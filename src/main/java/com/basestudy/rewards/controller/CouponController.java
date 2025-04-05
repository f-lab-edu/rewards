package com.basestudy.rewards.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.service.CouponService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("create")
    public  ApiResponseWrapper<?> createCoupon(@RequestBody CouponDto couponDto) {
        ApiResponseWrapper<?> res = couponService.createCoupon(couponDto);        
        return res;
    }
    //Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    //Date oldDate = Date.from(instant);
}
