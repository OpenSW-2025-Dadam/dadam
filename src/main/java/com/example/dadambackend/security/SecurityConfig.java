package com.example.dadambackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize 같은거 쓸 수 있게 (나중 대비)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 🔒 지금은 CSRF, 폼 로그인, HTTP Basic 다 꺼두기 (API 서버 기준)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 🔐 세션을 쓰지 않는 stateless 방식 (JWT 쓸 준비용)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🌐 CORS 기본 설정 사용 (필요하면 따로 config에서 CORS 설정한 거랑 연결)
                .cors(Customizer.withDefaults())

                // ✅ URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // ✅ Swagger / API 문서 경로 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // (혹시 예전 springfox 쓰고 있다면 이런 것도 필요할 수 있어)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/v2/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 🔓 그 외 모든 API도 일단 전부 허용 (임시)
                        .anyRequest().permitAll()
                );

        // 나중에 JWT 필터 추가할 때 여기에
        // http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
