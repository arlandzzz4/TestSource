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
            // 2. 토큰이 존재하고 유효성 검사를 통과하면 인증 객체 생성
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication auth = tokenProvider.getAuthentication(token);
                // 3. 스프링 시큐리티 컨텍스트에 인증 정보 저장 (이후 컨트롤러에서 @AuthenticationPrincipal 사용 가능)
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // [대비책] 필터 내부에서 예외가 발생해도 다음 필터는 계속 진행되거나, 여기서 401 에러를 처리할 수 있습니다.
            SecurityContextHolder.clearContext();
            //log.error("Security Context에 인증 정보를 설정할 수 없습니다.", e);
        }

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