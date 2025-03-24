package com.basestudy.rewards.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter{
    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationFilter(){
        super(new AntPathRequestMatcher("/api/signIn")); //일치하는 url 필터 동작
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        
        if(!request.getMethod().equals("POST")){
            throw new IllegalStateException("인증불가");
        }

        AuthDto authDto = objectMapper.readValue(request.getReader(), AuthDto.class);

        if(!StringUtils.hasLength(authDto.getEmail()) || !StringUtils.hasLength(authDto.getPassword())){
            throw new IllegalArgumentException("아이디 또는 패스워드가 입력되지 않았습니다.");
        }

        CustomAuthenticationToken token = new CustomAuthenticationToken(authDto.getEmail(), authDto.getPassword());

        Authentication authenticate = getAuthenticationManager().authenticate(token);

        return authenticate;
    }

    @Data
    public static class AuthDto {
        private String email;
        private String password;
    }
}
