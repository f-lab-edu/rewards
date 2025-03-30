package com.basestudy.rewards.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basestudy.rewards.controller.dto.SignUpDto;
import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("signUp")
    public ApiResponseWrapper<?> signUp(@RequestBody @Valid SignUpDto signUpDto) {
        return ApiResponseWrapper.createSuccess(memberService.signUp(signUpDto));
    }
    
    
}
