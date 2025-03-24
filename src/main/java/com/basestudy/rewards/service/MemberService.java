package com.basestudy.rewards.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.SignUpDto;

public interface MemberService {
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
    public ApiResponseWrapper<?> signUp(SignUpDto memberDto);
}
