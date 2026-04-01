package com.project.iob.user.contoller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.global.enums.Provider;
import com.project.iob.auth.service.AuthService;
import com.project.iob.user.dto.FcmTokenRequest;
import com.project.iob.user.dto.UserResponseDto;
import com.project.iob.user.entity.User;
import com.project.iob.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User API", description = "사용자 등록, 탈퇴, 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    
    @Operation(summary = "이메일로 유저 검색", description = "입력한 이메일과 일치하는 유저 정보를 조회합니다. 존재하지 않을 경우 404를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (유저 정보 반환)"),
        @ApiResponse(responseCode = "404", description = "해당 이메일을 가진 유저가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식")
    })
    @GetMapping("/search/{email}")
    public ResponseEntity<UserResponseDto> searchUserByEmail(
        @Parameter(description = "검색할 유저의 이메일", example = "user@example.com") 
        @PathVariable(value = "email") String email) {
    	User savedUser = userService.searchUserByEmail(email);
    	
    	// 엔티티 -> DTO 변환 (비밀번호 제외)
    	UserResponseDto response = UserResponseDto.from(savedUser);
        
        return ResponseEntity.ok(response); 
	}
    
    //탈퇴
    @Operation(summary = "회원 탈퇴", description = "유저의 계정을 삭제하고 관련된 모든 인증 정보를 무효화합니다. 성공 시 쿠키가 삭제됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공 (DB 삭제 및 로그아웃 완료)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자의 요청"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 정보")
    })
    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(
        @Parameter(description = "탈퇴할 유저 객체 (보통 현재 로그인된 정보 사용)") 
        @RequestBody User user, 
        HttpServletResponse response) {
    	
    	//탈퇴로직
    	// 1. DB에서 유저 정보 삭제
    	// userService.deleteUser(user.getId());
    	
    	// 2. FcmToken 등 인증 관련 정보 무효화 (로그아웃 처리)
    	 userService.updateFcmToken(user.getEmail(), null); // FCM 토큰 초기화 (선택적)
        
        // 3. DB에서 리프레시 토큰 무효화 (UserDetails를 통해 유저 식별)
    	authService.logout(Provider.LOCAL.getKey().equals(user.getProviderCode()) ? user.getEmail() : user.getProviderId(), user.getProviderCode());

        // 4. 쿠키 삭제 헤더 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0) 
                .build();

        // 5. 응답 바디에 로그아웃 성공 메시지 포함 (프론트엔드 처리 유도)
        Map<String, String> body = new HashMap<>();
        body.put("message", "Logout successful");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }
    
    @Operation(summary = "FcmToken", description = "유저의 FCM 토큰을 업데이트합니다. 이 엔드포인트는 인증된 사용자만 접근할 수 있으며, 성공적으로 업데이트되면 204 No Content를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "FCM 토큰 업데이트 성공 (응답 바디 없음)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자의 요청"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 정보"),
    })
    @PatchMapping("/me/fcmToken")
    public ResponseEntity<Void> updateFcmToken(
        @RequestBody FcmTokenRequest request,
        @AuthenticationPrincipal String email
    ) {
    	if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    	
        // UserService에서 해당 유저의 fcm_token 필드만 업데이트
        userService.updateFcmToken(email, request.fcmToken());
        return ResponseEntity.noContent().build();
    }
}