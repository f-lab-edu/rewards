package com.basestudy.rewards.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider{
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String loginId = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        Member member = (Member) memberService.loadUserByUsername(loginId);
        
        if(!password.equals(member.getPassword())){
            throw new BadCredentialsException("아이디 또는 비밀번호가 맞지않습니다.");
        }
        String jwtToken = jwtTokenUtil.generateJwtToken(authentication);
        return new CustomAuthenticationToken(member, null, member.getAuthorities(), jwtToken);
    }

    //여기서 목록을 찾아서 어떤 프로바이더를 사용할지 결정됨, obj에 둘이 다른 class를 줘야함
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }
}
