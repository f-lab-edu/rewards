package com.basestudy.rewards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.basestudy.rewards.security.CustomAuthenticationFilter;
import com.basestudy.rewards.security.CustomAuthenticationProvider;
import com.basestudy.rewards.security.JwtAuthenticationFilter;
import com.basestudy.rewards.security.JwtAuthenticationProvider;
import com.basestudy.rewards.security.JwtTokenUtil;
import com.basestudy.rewards.security.handler.CustomAccessDeniedHandler;
import com.basestudy.rewards.security.handler.CustomAuthenticationEntryPoint;
import com.basestudy.rewards.security.handler.CustomAuthenticationFailureHandler;
import com.basestudy.rewards.security.handler.CustomAuthenticationSuccessHandler;
import com.basestudy.rewards.service.MemberService;
import com.basestudy.rewards.service.MemberServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig{

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final MemberServiceImpl memberService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider; //AuthenticationProvider을 상속받지 않은 Provider
  
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //spring security v6~
        // AuthenticationManagerBuilder 생성
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        //authenticationManagerBuilder.userDetailsService(memberService).passwordEncoder(bCryptPasswordEncoder); //필요할경우 set
        
	    // AuthenticationManager 생성
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);
        // TODO: 나중에 영속성 컨텍스트때문에 발생할 문제
        // http.securityContext((securityContext) -> securityContext
        // .securityContextRepository(new DelegatingSecurityContextRepository(
        //         new RequestAttributeSecurityContextRepository(),
        //         new HttpSessionSecurityContextRepository()
        // )));
        http
            .csrf((csrfConfig)->
                csrfConfig.disable())
            .cors((corsConfig)->
                corsConfig.disable())
            .headers((headerConfig)->
                headerConfig.frameOptions(frameOptionsConfig->
                    frameOptionsConfig.disable()))
            .formLogin(formLoginConfig -> formLoginConfig.disable()) //UsernamePasswordAuthenticationFilter를 사용하지 않도록 설정, from로그인 사용할경우 주석처리 필요
            .sessionManagement(sessionManagement -> 
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용안함
            .authorizeHttpRequests((authorizeHttpRequests)->
                authorizeHttpRequests
                    //.requestMatchers("/signIn", "/signUp").permitAll()
                    //.requestMatchers("/admins/**").hasRole(Role.ADMIN.name()); 
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll())
                    //.anyRequest().authenticated())
            .authenticationProvider(this.getCustomAuthenticationProvider(memberService, jwtTokenUtil))
            .addFilterBefore(this.getCustomAuthenticationFilter(authenticationManager, jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(this.getJwtAuthenticationFilter(jwtAuthenticationProvider, jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling((exceptionConfig)->
                exceptionConfig
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler));
            
        return http.build();
    }

    private CustomAuthenticationFilter getCustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil){
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager);
        // customAuthenticationFilter.setSecurityContextHolderStrategy(null);
        customAuthenticationFilter.setAllowSessionCreation(false);
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return customAuthenticationFilter;
    }
    private CustomAuthenticationProvider getCustomAuthenticationProvider(MemberService memberService, JwtTokenUtil jwtTokenUtil){
        return new CustomAuthenticationProvider(memberService, null, jwtTokenUtil);
    }
    private JwtAuthenticationFilter getJwtAuthenticationFilter(JwtAuthenticationProvider jwtAuthenticationProvider, JwtTokenUtil jwtTokenUtil){
        return new JwtAuthenticationFilter(jwtAuthenticationProvider, jwtTokenUtil);
    }
}
