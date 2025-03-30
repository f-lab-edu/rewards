package com.basestudy.rewards.security;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter{
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/signIn",
    "POST"); //일치하는 url 필터 동작


    public CustomAuthenticationFilter(){
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
	}

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        //톰캣은 filter에서 request body를 읽으면 controller에서 body를 못읽음, > HttpServletRequestWrapper 클래스 사용으로 body를 계속 들고 있게함
        //이 코드에서 /signIn은 여기서밖에 처리 안하니까 body를 굳이 안들고 있어도 된다
        //@RequsetBody는 직렬화를 풀어준다, 여기서는 byte코드를 읽어 처리한다
        //getParameter 는 content-type : application/x-www-form-urlencoded 일 경우에 작동한다
       
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        AuthDto authDto = objectMapper.readValue(this.getRequestBody(request), AuthDto.class);
        
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        
        CustomAuthenticationToken token = new CustomAuthenticationToken(authDto.getEmail(), authDto.getPassword());
        
        Authentication authentication = getAuthenticationManager().authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private String getRequestBody(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("getRequestBody error");
        }
    
        return stringBuilder.toString();
    }

    @Data
    public static class AuthDto {
        private String email;
        private String password;

        public String getEmail(){
            return this.email != null ? this.email.trim() : "";
        }
        public String getPassword(){
            return this.password != null ? this.password.trim() : "";
        }
    }
}
