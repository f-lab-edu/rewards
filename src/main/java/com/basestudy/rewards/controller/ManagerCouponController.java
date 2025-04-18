package com.basestudy.rewards.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;



@Tag(name = "coupon-manager", description = "관리자 입장의 쿠폰발행 crud")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = ""),
    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/")
public class ManagerCouponController {
    private final CouponService couponService;

    @Operation(summary = "쿠폰 발행", description = "쿠폰을 생성합니다.", tags = {"coupon-manager"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "..")
    })
    @PostMapping("generate")
    public ApiResponseWrapper<?> generateCoupon(@RequestBody CouponDto couponDto) {
        ApiResponseWrapper<?> res = couponService.createCoupon(couponDto);        
        return res;
    }

    @Operation(summary = "쿠폰 정보 조회", description = "쿠폰 한개를 조회합니다.", tags = {"coupon-manager"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "조회된 정보가 없습니다.")
    })
    @GetMapping("get/{id}")
    public ApiResponseWrapper<CouponDto> getCoupon(@PathVariable long id) {
        ApiResponseWrapper<CouponDto> res = couponService.getCoupon(id);
        return res;
    }
    
    @Operation(summary = "쿠폰 발급시작", description = "쿠폰을 발급가능상태로 활성화합니다.", tags = {"coupon-manager"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "조회된 정보가 없습니다.")
    })
    @PutMapping("{id}")
    public ApiResponseWrapper<?> activeCoupon(@AuthenticationPrincipal Member member, @PathVariable long id) {
        ApiResponseWrapper<?> res = couponService.setInitialCouponQuantity(id);
        return res;
    }

    //Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    //Date oldDate = Date.from(instant);
}
