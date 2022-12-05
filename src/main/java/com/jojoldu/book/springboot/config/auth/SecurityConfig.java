package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers().frameOptions().disable()    // h2-console 화면을 사용하기 위해
            .and()
                .authorizeRequests()    // URL 별 권한 관리 시작
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())    // USER 권한 필요
                .anyRequest().authenticated()   // 나머지 URL 들은, 로그인한 사용자들만 접근
            .and()
                .logout()
                    .logoutSuccessUrl("/")
            .and()
                .oauth2Login()  // OAuth2 로그인 기능에 대한 설정 진입점
                    .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정 담당
                        .userService(customOAuth2UserService);  // 소셜 로그인 성공 시, 후속 조치를 진행할 UserService 인터페이스 구현체 등록
    }
}
