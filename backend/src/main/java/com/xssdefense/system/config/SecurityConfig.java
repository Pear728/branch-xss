package com.xssdefense.system.config;

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
        // 禁用CSRF保护
        http.csrf(csrf -> csrf.disable())
            // 禁用HTTP Basic认证
            .httpBasic(httpBasic -> httpBasic.disable())
            // 禁用表单登录
            .formLogin(formLogin -> formLogin.disable())
            // 允许所有请求通过，不需要身份验证
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        
        return http.build();
    }
} 