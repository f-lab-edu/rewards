package com.basestudy.rewards.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.repository.CouponRepository;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks // Mock을 주입할 대상 객체
    private CouponServiceImpl couponService;

    @Test
    @DisplayName("쿠폰등록성공")
    public void createCouponsSuccess(){
        //given
        CouponDto couponDto = CouponDto.builder()
                                        .code("COUPON")
                                        .availableFrom(LocalDateTime.now())
                                        .availableTo(LocalDateTime.now())
                                        .useDays(5)
                                        .totalAmount(100)
                                        .build();
        //when & then
        assertDoesNotThrow(()->couponService.createCoupon(couponDto));
    }

    @Test
    @DisplayName("쿠폰등록실패")
    public void createCouponWithNull(){
        //given
       CouponDto couponDto = CouponDto.builder()
                                        .code("")
                                        .availableFrom(LocalDateTime.now())
                                        .availableTo(LocalDateTime.now())
                                        .useDays(5)
                                        .totalAmount(100)
                                        .build();
        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->couponService.createCoupon(couponDto));
        
        assertEquals("필수값을 입력해주세요", exception.getMessage());
    }

}
