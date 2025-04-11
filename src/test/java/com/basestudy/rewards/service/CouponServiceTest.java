package com.basestudy.rewards.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.basestudy.rewards.contants.CouponStatus;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.entity.Coupon;
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
                                        .name("coupon")
                                        .availableFrom(LocalDateTime.now().plusDays(2))
                                        .availableTo(LocalDateTime.now().plusDays(3))
                                        .totalQuantity(100)
                                        .useDays(5)
                                        .build();
        //when & then
        assertDoesNotThrow(()->couponService.createCoupon(couponDto));
    }

    @Test
    @DisplayName("쿠폰등록실패")
    public void createCouponWithNull(){
        //given
       CouponDto couponDto = CouponDto.builder()
                                        .name("coupon")
                                        .availableFrom(LocalDateTime.now())
                                        .availableTo(LocalDateTime.now())
                                        .totalQuantity(0)
                                        .useDays(5)
                                        .build();
        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->couponService.createCoupon(couponDto));
        
        //assertEquals("필수값을 입력해주세요", exception..getMessage());
    }

    @Test
    @DisplayName("쿠폰변경실패")
    public void updateCouponsSuccess(){
        //given
        Coupon coupon = Coupon.builder()
                                .id(5L)
                                .name("테스트 쿠폰")
                                .availableFrom(LocalDateTime.now())
                                .availableTo(LocalDateTime.now().plusDays(1))
                                .totalQuantity(100)
                                .useDays(5)
                                .status(CouponStatus.ACTIVE) // 상태 설정
                                .build();

        // couponRepository.findById() Mock 설정
        when(couponRepository.findById(5L)).thenReturn(Optional.of(coupon));

        // couponRepository.save() Mock 설정
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // CouponDto 생성
        CouponDto couponDto = CouponDto.builder()
                                        .couponId(5L)
                                        .name("수정된 쿠폰")
                                        .availableFrom(LocalDateTime.now().plusDays(2))
                                        .availableTo(LocalDateTime.now().plusDays(3))
                                        .totalQuantity(200)
                                        .useDays(10)
                                        .build();

        //when & then
        assertDoesNotThrow(()->couponService.updateCoupon(couponDto));
    }

}
