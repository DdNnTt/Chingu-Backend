package com.chingubackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/static/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll() // 허용 경로
                        .anyRequest().permitAll() // 모든 요청 허용
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // H2 Console 내부 iframe 허용
                );

        return http.build();
    }
}