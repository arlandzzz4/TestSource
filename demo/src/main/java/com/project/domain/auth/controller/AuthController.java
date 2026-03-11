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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.domain.auth.dto.LoginRequestDto;
import com.project.domain.auth.dto.RegistUserRequestDto;
import com.project.domain.auth.dto.TokenDto;
import com.project.domain.auth.dto.UserResponseDto;
import com.project.domain.auth.entity.AuthUser;
import com.project.domain.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Auth API", description = "사용자 등록, 로그인, 로그아웃, 리프레시 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @GetMapping("/test2")
    public ResponseEntity<UserResponseDto> test2(@RequestParam(value = "email", defaultValue = "test@example.com") String email) {
    	AuthUser savedUser = authService.test2(email);
    	
    	// 엔티티 -> DTO 변환 (비밀번호 제외)
    	UserResponseDto response = UserResponseDto.from(savedUser);
        
        return ResponseEntity.ok(response); 
	}
    
    @Operation(summary = "회원가입", description = "새로운 유저를 등록하고 정보를 반환합니다. id가 생성되면 정상.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일입니다./이미 사용 중인 소셜 계정입니다.")
    })
    @PostMapping("/regist")
    public ResponseEntity<UserResponseDto> regist(@RequestBody RegistUserRequestDto registUserRequest) {
    	AuthUser savedUser = authService.regist(registUserRequest);
    	
    	// 엔티티 -> DTO 변환 (비밀번호 제외)
    	UserResponseDto response = UserResponseDto.from(savedUser);
        
        return ResponseEntity.ok(response);
	}

    @Operation(summary = "로그인", description = "유저 로그인 후 Access Token과 Refresh Token을 발급합니다. Refresh Token은 HttpOnly 쿠키로 저장됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일/비밀번호 불일치 등)")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        // 1. 서비스에서 토큰 발급 (성공 시 TokenDto 반환)
        TokenDto tokenDto = authService.login(loginRequest);
        
        //TODO 로그인 정보 외 필요한 정보 필요시
        

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
    
    @Operation(summary = "리프레시", description = "유저 로그인 상태 유지 위해 Access Token 재발급. Refresh Token은 쿠키 또는 Authorization 헤더에서 가져옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리프레시 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (Refresh Token 누락 등)")
    })
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
    
    @Operation(summary = "로그아웃", description = "유저 로그아웃 처리. DB에서 Refresh Token 무효화 및 클라이언트 쿠키 삭제를 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리프레시 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유저 정보 누락 등)")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody AuthUser authUser, 
            HttpServletResponse response) {
        
        // 1. DB에서 리프레시 토큰 무효화 (UserDetails를 통해 유저 식별)
        authService.logout("local".equalsIgnoreCase(authUser.getProvider()) ? authUser.getEmail() : authUser.getProviderId(), authUser.getProvider());

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
}