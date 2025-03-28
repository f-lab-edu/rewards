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
import com.basestudy.rewards.security.handler.CustomAccessDeniedHandler;
import com.basestudy.rewards.security.handler.CustomAuthenticationEntryPoint;
import com.basestudy.rewards.service.MemberServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig{

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final MemberServiceImpl memberServiceImpl;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //spring security v6~
        // AuthenticationManagerBuilder 생성
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(memberServiceImpl);//.passwordEncoder(bCryptPasswordEncoder);
        
	    // AuthenticationManager 생성
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http
            .csrf((csrfConfig)->
                csrfConfig.disable())
            .cors((corsConfig)->
                corsConfig.disable())
            .headers((headerConfig)->
                headerConfig.frameOptions(frameOptionsConfig->
                    frameOptionsConfig.disable()))
            //.formLogin(formLoginConfig -> formLoginConfig.disable()) //UsernamePasswordAuthenticationFilter를 사용하지 않도록 설정하는거라는데 있던 없던 안탐
            .sessionManagement(sessionManagement -> 
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용안함
            .authorizeHttpRequests((authorizeHttpRequests)->
                authorizeHttpRequests
                    .requestMatchers("/signIn", "/signUp").permitAll()
                    //.requestMatchers("/admins/**").hasRole(Role.ADMIN.name()); 
                    // .requestMatchers("/api/**").authenticated()
                    // .anyRequest().permitAll())
                    .anyRequest().authenticated())
            .authenticationManager(authenticationManager)
            .addFilterBefore(getCustomAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling((exceptionConfig)->
                exceptionConfig
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler));
            
        return http.build();
    }

    private CustomAuthenticationFilter getCustomAuthenticationFilter(AuthenticationManager authenticationManager){
        return new CustomAuthenticationFilter(authenticationManager);
    }

}
