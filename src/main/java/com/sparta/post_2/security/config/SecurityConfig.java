package com.sparta.post_2.security.config;


import com.sparta.post_2.security.jwt.JwtAccessDeniedHandler;
import com.sparta.post_2.security.jwt.JwtAuthenticationEntryPoint;
import com.sparta.post_2.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web){
        //h2 database 테스트를 위해 관련 api 전부 무시
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //CSRF 설정
        http.csrf().disable()

                // exception handling
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                //h2 콘솔 설정 추가
                .and()
                    .headers()
                    .frameOptions()
                    .sameOrigin()
                // 시큐리티 기본적으로 세션을 사용하지만 여기서는 세션을 사용 안해서 stateless로 설정
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //login, signup api는 토큰이 없는 상태에서 요청이 들어와 막히지 않게 permitALL 설정해줘야 함
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/all/**").permitAll()
                .anyRequest().authenticated() //나머지 api는 인증 후 사용 가능
                //JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스 적용
                .and()
                    .apply(new JwtSecurityConfig(tokenProvider));
    }
}
