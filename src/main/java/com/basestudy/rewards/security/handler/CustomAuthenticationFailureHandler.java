package com.basestudy.rewards.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.basestudy.rewards.ApiResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponseWrapper<?> apiResponseWrapper = ApiResponseWrapper.createFail(null, "401", getExceptionMessage(exception)); //HttpStatus.UNAUTHORIZED
        String message = objectMapper.writeValueAsString(apiResponseWrapper);
        
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(message);
        // response.getWriter().flush();
        // response.flushBuffer();
    }

    private String getExceptionMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "아이디 또는 비밀번호가 맞지않습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            return "일치하는 사용자가 없습니다.";
        } else if (exception instanceof AccountExpiredException) {
            return "사용자 계정이 만료되었습니다.";
        } else if (exception instanceof CredentialsExpiredException) { //이거는 상황따라 다르게 사용가능할듯
            return "비밀번호 유효기간이 만료되었습니다.";
        } else if (exception instanceof DisabledException) {
            return "휴면계정 상태입니다.";
        } else if (exception instanceof LockedException) {
            return "계정이 잠겼습니다.";
        } else {
            return "사용자 인증에 실패했습니다.";
        }
    }
}
