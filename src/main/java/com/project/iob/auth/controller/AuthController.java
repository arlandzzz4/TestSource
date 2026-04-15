package com.project.iob.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.global.enums.Provider;
import com.project.iob.auth.dto.LoginRequestDto;
import com.project.iob.auth.dto.LoginResponseDto;
import com.project.iob.auth.dto.LogoutRequestDto;
import com.project.iob.auth.dto.TokenDto;
import com.project.iob.auth.service.AuthService;
import com.project.iob.user.dto.UserAuthRequestDto;
import com.project.iob.user.entity.User;
import com.project.iob.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final UserService userService;
    
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 인증하며, 성공 시 Access Token과 HttpOnly Refresh Cookie를 발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공 (Body: AccessToken, Header: Set-Cookie 포함)"),
        @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 불일치)"), // 400보다는 401이 적절
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/login")
    @Transactional
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response)  {
        // 1. 서비스에서 토큰 발급 (성공 시 TokenDto 반환)
        TokenDto tokenDto = authService.login(loginRequest);
        return loginReturn(tokenDto, loginRequest.email());
    }
    
    @Operation(summary = "토큰 재발급", description = "만료된 Access Token을 교체하기 위해 사용합니다. Cookie에 담긴 Refresh Token을 자동으로 검증합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "재발급 성공 (새로운 Access Token 발급)"),
        @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 Refresh Token"),
        @ApiResponse(responseCode = "400", description = "쿠키에 Refresh Token이 존재하지 않음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(
        @Parameter(hidden = true) // Swagger UI에서 수동 입력을 막고 쿠키 전송임을 명시
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
    
    @Operation(summary = "로그아웃", description = "서버 측의 Refresh Token을 무효화하고, 브라우저의 전용 쿠키를 만료(삭제) 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공 (클라이언트 쿠키 삭제됨)"),
        @ApiResponse(responseCode = "401", description = "이미 만료된 세션이거나 잘못된 요청")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
        @Parameter(description = "로그아웃할 유저 정보", required = true) 
        @RequestBody LogoutRequestDto logout, 
        HttpServletResponse response) {
    	
    	// FCM 토큰 삭제 (비우기)
        userService.clearFcmToken(logout.email());
        
        // 1. DB에서 리프레시 토큰 무효화 (UserDetails를 통해 유저 식별)
        authService.logout(Provider.LOCAL.getKey().equals(logout.providerCode()) ? logout.email() : logout.providerId(), logout.providerCode());

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
    
    @Operation(summary = "회원가입", description = "새로운 유저 정보를 등록합니다. 중복된 이메일이나 소셜 계정은 가입이 거부됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공 (신규 유저 정보 반환)"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패 (비밀번호 형식 등)"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 소셜 계정")
    })
    @PostMapping("/regist")
    public ResponseEntity<LoginResponseDto> regist(@RequestBody UserAuthRequestDto UserRequest) {
    	TokenDto tokenDto = userService.regist(UserRequest);
    	
    	return loginReturn(tokenDto, UserRequest.email());
	}
    
    private ResponseEntity<LoginResponseDto> loginReturn(TokenDto tokenDto, String email){
    	if(tokenDto.isSuccess()) {
        	User user = userService.searchUserByEmail(email);
        	
            // 2. Refresh Token을 HttpOnly 쿠키에 저장 (보안 강화)
            ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.refreshToken())
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    //.sameSite("Strict")
                    .sameSite("Lax")
                    .maxAge(60 * 60 * 24 * 7) // 7일
                    .build();

            // 3. 쿠키를 헤더에 추가하고, 바디에는 Access Token이 포함된 DTO를 담아 반환
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(LoginResponseDto.builder()
                            .accessToken(tokenDto.accessToken()) // ★ 수정: refreshToken 넣지 않도록 주의
                            .user(user)
                            .isSuccess(true)
                            .build());
        }else {
        	// 로그인 실패 시 적절한 에러 메시지와 상태 코드 반환 (예: 401 Unauthorized)
			return ResponseEntity.ok().body(LoginResponseDto.builder()
					.isSuccess(tokenDto.isSuccess())
					.message(tokenDto.message())
					.build());
        }
    }
    
}