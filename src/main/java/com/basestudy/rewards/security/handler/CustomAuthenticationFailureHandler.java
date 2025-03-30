package com.basestudy.rewards.security.handler;

import java.io.IOException;

import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.LoginException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
        ApiResponseWrapper.createFail(null, "401", getExceptionMessage(exception)); //HttpStatus.UNAUTHORIZED
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
