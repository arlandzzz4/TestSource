package com.project.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project.global.error.JwtAccessDeniedHandler;
import com.project.global.error.JwtAuthenticationEntryPoint;
import com.project.global.security.JwtAuthenticationFilter;
import com.project.global.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF & CORS 설정
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. 세션 정책: JWT 사용으로 인한 STATELESS 설정
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 3. 권한 정밀 설정 (중요: 순서가 중요함)
            .authorizeHttpRequests(auth -> auth
                // 로그인, 회원가입 등 공통 허용
                .requestMatchers("/api/auth/**").permitAll()
                // Swagger UI 및 API 문서 허용 (개발 시 필수)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                // 관리자 전용 API
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 4. JWT 필터 위치 지정
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), 
                            UsernamePasswordAuthenticationFilter.class)
            
            // 5. 예외 처리 (인증 실패 및 권한 부족 시 응답 커스텀)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized"))
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied"))
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401 핸들러 등록
                .accessDeniedHandler(jwtAccessDeniedHandler)           // 403 핸들러 등록
            );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 내부에 '솔트(Salt)'를 자동으로 포함하며, 
        // 하드웨어 성능 향상에 따라 연산 속도를 조절(Cost factor)할 수 있어 브루트 포스 공격에 강합니다.
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 허용할 프론트엔드 도메인 (Vite 기본값: 5173)
        // [주의] allowCredentials(true)일 때는 와일드카드(*)를 사용할 수 없습니다.
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // 2. 허용할 HTTP 메서드
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. 허용할 헤더 (Authorization, Content-Type 등)
        config.setAllowedHeaders(List.of("*"));

        // 4. 클라이언트가 응답에서 읽을 수 있는 헤더 (JWT 토큰 등을 바디가 아닌 헤더로 보낼 때 필요)
        config.setExposedHeaders(List.of("Authorization"));

        // 5. [핵심] 내 자격 증명(쿠키, 인증 헤더 등)을 다른 도메인에 보낼 수 있도록 허용
        // Refresh Token을 HttpOnly 쿠키로 주고받으려면 반드시 true여야 합니다.
        config.setAllowCredentials(true);

        // 6. 브라우저가 CORS 체크(Preflight) 결과를 캐싱할 시간 (초 단위)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 API 경로(/**)에 대해 위 설정을 적용
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }
    
}