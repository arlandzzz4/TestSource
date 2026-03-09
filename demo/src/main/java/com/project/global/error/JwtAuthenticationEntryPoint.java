package com.project.global.error;

import java.io.IOException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException authException)
			throws IOException, ServletException {
		// 1. 인코딩 및 상태 설정
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2. 필터에서 넘겨준 상세 예외 코드 확인
        String exception = (String) request.getAttribute("exception");
        ErrorResponse errorResponse;

        if ("TOKEN_EXPIRED".equals(exception)) {
            errorResponse = ErrorResponse.of("EXPIRED_TOKEN", "액세스 토큰이 만료되었습니다. 재발급이 필요합니다.");
        } else if ("TOKEN_INVALID".equals(exception)) {
            errorResponse = ErrorResponse.of("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
        } else {
            errorResponse = ErrorResponse.of("UNAUTHORIZED", "로그인이 필요한 서비스입니다.");
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}