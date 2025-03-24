package com.basestudy.rewards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import com.basestudy.rewards.security.CustomAuthenticationFilter;
import com.basestudy.rewards.security.handler.CustomAccessDeniedHandler;
import com.basestudy.rewards.security.handler.CustomAuthenticationEntryPoint;
import com.basestudy.rewards.security.handler.CustomAuthenticationFailureHandler;
import com.basestudy.rewards.security.handler.CustomAuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig{

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf((csrfConfig)->
                csrfConfig.disable())
            .cors((corsConfig)->
                corsConfig.disable())
            .headers((headerConfig)->
                headerConfig.frameOptions(frameOptionsConfig->
                    frameOptionsConfig.disable()))
            .authorizeHttpRequests((authorizeHttpRequests)->
                authorizeHttpRequests
                    .requestMatchers("/api/signIn", "/api/signUp").permitAll()
                    //.requestMatchers("/admins/**").hasRole(Role.ADMIN.name()); 
                    // .requestMatchers("/api/**").authenticated()
                    // .anyRequest().permitAll())
                    .anyRequest().authenticated())
            .addFilterBefore(ajaxAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling((exceptionConfig)->
                exceptionConfig
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler));
  
        return http.build();
    }

    @Bean
    CustomAuthenticationFilter ajaxAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
        customAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
       
        // **
        customAuthenticationFilter.setSecurityContextRepository(
                new DelegatingSecurityContextRepository(
                        new RequestAttributeSecurityContextRepository(),
                        new HttpSessionSecurityContextRepository()
                ));

        return customAuthenticationFilter;
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // @Bean
    // UserDetailsService userDetailsService() {
    //     return memberService;
    // }

    // @Bean
    // public PasswordEncoder passwordEncoder(){
    //     return new BCryptPasswordEncoder();
    // }
}
