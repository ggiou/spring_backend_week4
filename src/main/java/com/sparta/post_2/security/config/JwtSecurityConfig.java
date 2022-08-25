package com.sparta.post_2.security.config;

import com.sparta.post_2.security.jwt.JwtFilter;
import com.sparta.post_2.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
    private final TokenProvider tokenProvider;

    //TokenProvider을 받아서 JwtFilter를 통해 Security 로직에 필터 등록
    @Override
    public void configure(HttpSecurity httpSecurity){
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        httpSecurity.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
