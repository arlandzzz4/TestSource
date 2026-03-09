package com.project.global.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
    	// 1. 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        try {
            // 2. 토큰이 존재할 때만 검증 로직 수행
            if (StringUtils.hasText(token)) {
                if (tokenProvider.validateToken(token)) {
                    Authentication auth = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                // 주의: 여기서 else 처리를 해서 예외를 던지면 permitAll 구간도 401이 뜹니다.
                // 유효하지 않은 토큰이라도 일단 통과시켜야 SecurityConfig의 permitAll()이 판단합니다.
            }
        } catch (Exception e) {
        	// EntryPoint에서 읽을 수 있도록 에러 정보를 request에 담아줍니다.
            request.setAttribute("exception", "TOKEN_INVALID");
            // 필터 내부 에러가 전체 요청을 막지 않도록 보장
            SecurityContextHolder.clearContext();
        }

        // 3. 반드시 다음 필터로 넘겨야 SecurityConfig의 설정(permitAll 등)을 타게 됩니다.
        filterChain.doFilter(request, response);
    }

    /**
     * 헤더에서 토큰 추출 (Bearer prefix 제거)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // [참고] startsWithIgnoreCase를 쓰면 대소문자 구분 없이 더 안전하게 체크 가능합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}