package com.project.domain.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.domain.auth.dto.LoginRequest;
import com.project.domain.auth.dto.TokenDto;
import com.project.domain.auth.entity.Users;
import com.project.domain.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // 1. 서비스에서 토큰 발급 (성공 시 TokenDto 반환)
        TokenDto tokenDto = authService.login(loginRequest);
        
        //TODO 로그인 정보 외 필요한 정보
        log.debug("test~~~~~~~~~~~~~~~~~~~");
        

        // 2. Refresh Token을 HttpOnly 쿠키에 저장 (보안 강화)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.refreshToken())
                .path("/")
                .httpOnly(true)
                .secure(true) // HTTPS 환경 필수
                .sameSite("Strict")
                .maxAge(60 * 60 * 24 * 7) // 7일
                .build();

        // 3. 쿠키를 헤더에 추가하고, 바디에는 Access Token이 포함된 DTO를 담아 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(TokenDto.builder()
                        .accessToken(tokenDto.refreshToken()) // 쿠키 차단 대비용 (보조)
                        .build());
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(
    		@CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
            HttpServletRequest request) {
    	// 1. 쿠키에 없으면 헤더(Authorization)에서 가져오기 시도
        String refreshToken = cookieRefreshToken;
        if (refreshToken == null || refreshToken.isBlank()) {
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                refreshToken = bearerToken.substring(7);
            }
        }

        // 2. 둘 다 없으면 에러 처리 (또는 커스텀 예외)
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh Token이 존재하지 않습니다.");
        }

        TokenDto tokenDto = authService.reissue(refreshToken);
        return ResponseEntity.ok(tokenDto);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody Users users, 
            HttpServletResponse response) {
        
        // 1. DB에서 리프레시 토큰 무효화 (UserDetails를 통해 유저 식별)
        authService.logout("local".equalsIgnoreCase(users.getProvider()) ? users.getEmail() : users.getProviderId(), users.getProvider());

        // 2. 쿠키 삭제 헤더 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0) 
                .build();

        // 3. 응답 바디에 로그아웃 성공 메시지 포함 (프론트엔드 처리 유도)
        Map<String, String> body = new HashMap<>();
        body.put("message", "Logout successful");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }
    @GetMapping("/me")
    public void test() {
    	log.debug("test~~~~~~~~~~~~~~~~~~~");
    }
}