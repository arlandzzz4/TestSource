package com.project.domain.user.contoller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.domain.auth.service.AuthService;
import com.project.domain.user.dto.UserRequestDto;
import com.project.domain.user.dto.UserResponseDto;
import com.project.domain.user.entity.User;
import com.project.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "이메일로 유저 검색", description = "이메일을 통해 유저 정보를 조회합니다. id가 존재하면 정상.")
    @GetMapping("/search/{email}")
    public ResponseEntity<UserResponseDto> searchUserByEmail(@PathVariable(value = "email") String email) {
    	User savedUser = userService.searchUserByEmail(email);
    	
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
    public ResponseEntity<UserResponseDto> regist(@RequestBody UserRequestDto UserRequest) {
    	User savedUser = userService.regist(UserRequest);
    	
    	// 엔티티 -> DTO 변환 (비밀번호 제외)
    	UserResponseDto response = UserResponseDto.from(savedUser);
        
        return ResponseEntity.ok(response);
	}
    
    //탈퇴
    @Operation(summary = "탈퇴", description = "")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리프레시 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유저 정보 누락 등)")
    })
    @PostMapping("/Unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(
            @RequestBody User user, 
            HttpServletResponse response) {
    	
    	//탈퇴로직
        
        // 1. DB에서 리프레시 토큰 무효화 (UserDetails를 통해 유저 식별)
    	authService.logout("local".equalsIgnoreCase(user.getProvider()) ? user.getEmail() : user.getProviderId(), user.getProvider());

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