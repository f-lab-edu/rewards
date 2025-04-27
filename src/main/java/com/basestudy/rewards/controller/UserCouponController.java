package com.basestudy.rewards.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.service.UserCouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "coupon-user", description = "사용자 입장의 쿠폰발급")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = ""),
    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/")
public class UserCouponController {
    private final UserCouponService userCouponService;

    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다.", tags = {"coupon-user"})
    @PostMapping("distribute")
    public ApiResponseWrapper<?> distributeCoupon(@AuthenticationPrincipal Member member, @RequestBody Map<String, Long> reqbody) {
        return userCouponService.distributeCoupon(member, reqbody.get("couponId"));
    }
}
