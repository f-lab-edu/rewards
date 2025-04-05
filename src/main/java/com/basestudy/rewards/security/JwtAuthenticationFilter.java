package com.basestudy.rewards.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.basestudy.rewards.ApiResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";
    private static final int TOKEN_TYPE_LENGTH = TOKEN_TYPE.length() + 1;
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/signIn", "POST");

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtTokenUtil jwtTokenUtil;
    
    public JwtAuthenticationFilter(JwtAuthenticationProvider jwtAuthenticationProvider, JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
    
            log.debug("인증 요청 시작: {}", request.getRequestURI());
            
            String authorizationHeader = request.getHeader(HEADER_NAME);
            String jwtToken = extractToken(authorizationHeader);

            if (jwtToken != null && isValidToken(jwtToken)) {
                String username = jwtTokenUtil.getUserName(jwtToken);
                log.debug("유효한 JWT 토큰 발견: 사용자={}", username);

                //oncePerRequestFilter는 security에서 provider를 지원하지 않음
                Authentication authentication =jwtAuthenticationProvider.authenticate(username);                                

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("인증 성공: 사용자={}", username);
            } else if(DEFAULT_ANT_PATH_REQUEST_MATCHER.matches(request) && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
                //success handler, fail handler도 AbstractAuthenticationProcessingFilter 이거 상속받은 필터에게만 허용됨, 철저히 로그인을 위한 프로세스네, 원래는 페이지 전환등의 책임을 가짐,,
                //가장처음 AbstractAuthenticationProcessingFilter 인증관련 필터는 성공시 바로 success handler 호출,여기로 오지도 않음
                //ㄴㄴ얘는 (상속받은 내용에의해) 문제없으면 다음필터로 통과시키는 애지 성공했다고 뭘 처리하는 애가 아님
                log.debug("인증 성공: 로그인 프로세스");
            } else {
                //필터에서 나는 오류는 controll advice로 처리 불가
                //dofilter의 exception은 전파 안됨, servletExcpion으로 래핑해야 전파 가능
                //login용으로 handler가 따로 존재하는 이유가 있네, ExceptionTranslationFilter앞에 있는 필터들에 한해 처리됨
                //잘못된사용->throw new AuthenticationException("인증 헤더 없거나 유효하지 않은 토큰입니다.");
                //handleAuthenticationFailure(response, "인증 헤더 없거나 유효하지 않은 토큰입니다.");
                log.info("JwtAuthenticationFilter ::: 헤더에 토큰 없음");
            }

        filterChain.doFilter(request, response);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_TYPE)) {
            return null;
        }
        return authorizationHeader.substring(TOKEN_TYPE_LENGTH);
    }

    private boolean isValidToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }

    // private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     ApiResponseWrapper<?> apiResponseWrapper = ApiResponseWrapper.createFail(null, "401", message); //HttpStatus.UNAUTHORIZED
        
    //     // response.setStatus(HttpStatus.OK.value());
    //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    //     response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    //     response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    //     response.getWriter().write(objectMapper.writeValueAsString(apiResponseWrapper));//명시적 close안하고 컨테이너가 자동관리
    // }
}
