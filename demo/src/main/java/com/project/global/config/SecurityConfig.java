package com.project.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
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
            
            // 3. 권한 설정: 구체적인 것부터 넓은 범위 순으로!
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/error", "/favicon.ico").permitAll()
                // Swagger 및 문서 관련 (누구나)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                
                // TODO: 설정을 통해 정적 리소스를 보안 대상에서 제외
                .requestMatchers("/img/**").permitAll() 
                
                // TODO: 인증이 필요한 API와 익명 사용자만 접근 가능한 API를 명확히 구분하여 설정
                // 로그인, 회원가입은 '익명 사용자'만 접근 가능하게 설정
                //.requestMatchers("/api/auth/login", "/api/auth/regist").anonymous()
                .requestMatchers("/api/auth/login", "/api/auth/regist").permitAll()
                // 그 외 /api/auth/** 하위의 다른 기능(로그아웃 등)은 누구나 가능하게 두거나
                // 필요에 따라 아래처럼 분리할 수 있습니다.
                .requestMatchers("/api/auth/logout", "/api/auth/reissue").authenticated()
                //.requestMatchers("/api/auth/**").permitAll() 
                
                // 관리자 API (권한 필요)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 그 외 모든 요청은 인증 필요 (4주 프로젝트 보안상 권장)
                //.anyRequest().authenticated() 
                .anyRequest().permitAll()
            )
            // 4. 불필요한 기본 로그인창 비활성화
            .formLogin(form -> form
                // 1. 사용자 정의 로그인 페이지 경로 설정 (기본값은 /login)
                .loginPage("/api/auth/login")
                // 2. 로그인 성공 시 이동할 기본 경로
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .httpBasic(basic -> basic.disable())
            
            // 5. 필터 순서
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), 
                            UsernamePasswordAuthenticationFilter.class)
            
            // 6. 예외 처리 (중복 제거 및 명확한 핸들러 지정)
            .exceptionHandling(ex -> ex
                // 인증 실패 시 (401) - 반드시 JSON 응답을 주거나 SC_UNAUTHORIZED를 리턴해야 함
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) 
                // 권한 부족 시 (403)
                .accessDeniedHandler(jwtAccessDeniedHandler)
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