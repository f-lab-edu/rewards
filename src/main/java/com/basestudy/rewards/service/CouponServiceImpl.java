package com.basestudy.rewards.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.CouponDto;
import com.basestudy.rewards.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;

    @Transactional()
    public ApiResponseWrapper<?> createCoupon(CouponDto couponDto) throws RuntimeException{
        if(!valid(couponDto)){
            throw new IllegalArgumentException("필수값을 입력해주세요");
        }
        couponRepository.save(couponDto.toEntity());
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }


    
    //TODO: 길이체크 추가 등
    private boolean valid(CouponDto couponDto){
        if(couponDto.getCode().isBlank() ||
            couponDto.getAvailableTo()==null ||
            couponDto.getAvailableFrom()==null ||
            couponDto.getUseDays()==0 ||
            couponDto.getTotalAmount()==0){
                return false;
            }
        return true;
    }
}
