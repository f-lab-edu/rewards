package com.basestudy.rewards.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.basestudy.rewards.entity.Member;
import com.basestudy.rewards.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider{
    private final MemberService memberService;

    public Authentication authenticate(String loginId) throws AuthenticationException{
        Member member = (Member) memberService.loadUserByUsername(loginId);
        //TODO: 권한등의 확인 또는 탈퇴, 휴면 등의 상태확인 구현
        return new CustomAuthenticationToken(member, null, member.getAuthorities());
    }
}
