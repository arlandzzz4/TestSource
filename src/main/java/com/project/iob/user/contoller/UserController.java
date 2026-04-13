package com.project.iob.user.contoller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.global.enums.Provider;
import com.project.iob.auth.service.AuthService;
import com.project.iob.user.dto.FcmTokenRequest;
import com.project.iob.user.dto.PasswordChangeRequestDto;
import com.project.iob.user.dto.UnsubscribeRequestDto;
import com.project.iob.user.dto.UpdateNicknameRequest;
import com.project.iob.user.dto.UserAuthResponseDto;
import com.project.iob.user.dto.UserRequestDto;
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
    public ResponseEntity<UserAuthResponseDto> searchUserByEmail(
        @Parameter(description = "검색할 유저의 이메일", example = "user@example.com") 
        @PathVariable(value = "email") String email) {
    	User savedUser = userService.searchUserByEmail(email);
    	
    	// 엔티티 -> DTO 변환 (비밀번호 제외)
    	UserAuthResponseDto response = UserAuthResponseDto.from(savedUser);
        
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
        @RequestBody UnsubscribeRequestDto unsubscribeRequestDto, 
        HttpServletResponse response) {
    	
    	// FcmToken 등 인증 관련 정보 무효화 (로그아웃 처리)
    	//userService.updateFcmToken(unsubscribeRequestDto.email(), null); // FCM 토큰 초기화 (선택적)
    	// service에서 unscribe와 clearFcmToken을 합쳐놓음 -> 밑에서 알아서 처리 될것임
    	 
    	//탈퇴로직 (FCM 초기화 + 상태 변경이 한 번에 일어남)
     	// DB에서 유저 정보 삭제
    	userService.unsubscribe(unsubscribeRequestDto);
        
        // DB에서 리프레시 토큰 무효화 (UserDetails를 통해 유저 식별)
    	authService.logout(Provider.LOCAL.getKey().equals(unsubscribeRequestDto.providerCode()) ? unsubscribeRequestDto.email() : unsubscribeRequestDto.providerId(), unsubscribeRequestDto.providerCode());

        // 쿠키 삭제 헤더 생성
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
    
    @Operation(summary = "총 유저수 검색", description = "전체 유저 수를 조회합니다. 성공 시 총 유저 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (총 유저 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 유저가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/totalcnt")
    public ResponseEntity<Integer> searchUserTotalCount(
    		@ParameterObject UserRequestDto userRequestDto
    		) {
    	int cnt = userService.searchUserCount(userRequestDto); // 전체 유저 수 조회 (날짜 조건 없이)
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "오늘 가입 유저수 검색", description = "오늘 가입한 유저 수를 조회합니다. 성공 시 오늘 가입한 유저 수를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (오늘 가입한 유저 수 반환)"),
        @ApiResponse(responseCode = "404", description = "존재하는 유저가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/todaycnt")
    public ResponseEntity<Integer> searchUserTodayCount(){
    	//오늘
    	String today = java.time.LocalDate.now().toString();
    	//한달 전 
    	String oneMonthAgo = java.time.LocalDate.now().minusMonths(1).toString();
    	log.info("today: {}, oneMonthAgo: {}", today, oneMonthAgo);
    	
    	UserRequestDto userRequestDto = new UserRequestDto(oneMonthAgo, today);
    	int cnt = userService.searchUserCount(userRequestDto);
    	
        return ResponseEntity.ok(cnt); 
	}
    
    @Operation(summary = "사용자 조회(페이징)", description = "사용자를 페이징하여 조회합니다. 페이지 번호와 페이지 크기를 쿼리 파라미터로 전달받아 해당 페이지의 사용자 목록을 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (사용자 정보 반환)"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/search/user")
    public ResponseEntity<List<UserResponseDto>> searcUsers(
    		@ParameterObject UserRequestDto userRequestDto
    		){
    	List<UserResponseDto> users = userService.searchUsers(userRequestDto);
    	
        return ResponseEntity.ok(users); 
	}
    
    @Operation(summary = "사용자 상태코드 수정", description = "사용자 상태코드 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 상태코드 업데이트 성공 (응답 바디 없음)"),
        @ApiResponse(responseCode = "404", description = "잘못된 요청"),
    })
    @PatchMapping("/updateUserStatusCode")
    public ResponseEntity<Void> updateUserStatusCode(
    		@RequestBody UserRequestDto userRequestDto
    ) {
        userService.updateUserStatusCode(userRequestDto);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PatchMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(
            @RequestBody UpdateNicknameRequest request // 전용 DTO로 변경
    ) {
        // 기존 로직을 유지하기 위해 UserRequestDto로 변환하여 서비스 호출
        // 또는 서비스 인터페이스를 수정하여 파라미터를 최적화할 수 있습니다.
        UserRequestDto userRequestDto = new UserRequestDto(
            request.email(), 
            request.nickname(), 
            null, null, null, null, null, null, null
        );
        userService.updateNickname(userRequestDto);
        return ResponseEntity.noContent().build();
    }
    
    //비번 변경
    @Operation(summary = "비밀번호 변경", description = "Firebase 재인증 완료 후 DB 비밀번호를 동기화합니다. 일반 회원(LOCAL)만 가능합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "변경 성공"),
        @ApiResponse(responseCode = "400", description = "소셜 로그인 회원은 변경 불가"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저")
    })
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody PasswordChangeRequestDto dto
    ) {
        userService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "이메일로 유저 검색", description = "입력한 이메일과 일치하는 유저 정보를 조회합니다. 존재하지 않을 경우 404를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공 (유저 정보 반환)"),
        @ApiResponse(responseCode = "404", description = "해당 이메일을 가진 유저가 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식")
    })
    @GetMapping("/search/nickname")
    public ResponseEntity<Integer> searchNickname( 
        @RequestParam(value = "nickname") String nickname) {
    	int cnt = userService.searchNickname(nickname);
    	
    	return ResponseEntity.ok(cnt); 
	}
}