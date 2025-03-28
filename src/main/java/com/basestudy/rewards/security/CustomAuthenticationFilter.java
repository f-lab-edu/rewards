package com.basestudy.rewards.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter{
    private static final String USERNAME_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/signIn",
    "POST"); //일치하는 url 필터 동작

    public CustomAuthenticationFilter(){
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER); 
    }

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
	}

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
		String username = obtainUsername(request);
		username = (username != null) ? username.trim() : "";
		String password = obtainPassword(request);
		password = (password != null) ? password : "";

        CustomAuthenticationToken token = new CustomAuthenticationToken(username, password);
        
        Authentication authenticate = getAuthenticationManager().authenticate(token);

        return authenticate;
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request){
        return request.getParameter(USERNAME_KEY);
    }
    @Nullable
    protected String obtainPassword(HttpServletRequest request){
        return request.getParameter(PASSWORD_KEY);
    }
}
