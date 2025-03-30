package com.basestudy.rewards.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.basestudy.rewards.entity.Member;
import com.basestudy.rewards.service.MemberService;
import com.basestudy.rewards.service.MemberServiceImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider{
    private final MemberService memberService;
    //TODO: private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String loginId = authentication.getName();
        String password = (String) authentication.getCredentials();

        Member member = (Member) memberService.loadUserByUsername(loginId);
        
        if(password.equals(member.getPassword())){
            throw new BadCredentialsException("아이디 또는 비밀번호가 맞지않습니다.");
        }

        return new CustomAuthenticationToken(member, null, member.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }
}
