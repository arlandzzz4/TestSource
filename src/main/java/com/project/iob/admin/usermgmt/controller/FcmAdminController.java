package com.project.iob.admin.usermgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.admin.usermgmt.dto.FcmRequestDto;
import com.project.iob.admin.usermgmt.service.FcmAdminService;
import com.project.iob.user.entity.User;
import com.project.iob.user.querydsl.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User API", description = "사용자 등록, 탈퇴, 조회 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/fcm")
public class FcmAdminController {

    private final FcmAdminService fcmAdminService;
    private final UserRepository userRepository; // 유저 토큰 조회를 위해 필요

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody FcmRequestDto request) {
        // 실제로는 DB에서 해당 유저의 토큰을 가져와야 함
    	// TODO: 유저 ID로 DB에서 FCM 토큰 조회 로직 추가 (UserRepository 활용)
    	// 테이블 구조에 따라 조회가 달라저 임시로 만들어 놓음. 조회조건에 따라 로직 및 DTO 수정 필요.(다수 일 경우 반복문으로 처리)
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다.")); // 예시로 ID 1번 유저 조회
        
        if (user.getFcmToken() != null) {
        	fcmAdminService.sendMessage(
        		user.getFcmToken(), 
                request.title(), 
                request.body(),
                request.path()
            );
            return ResponseEntity.ok("전송 요청 성공");
        }
        return ResponseEntity.badRequest().body("유저에게 등록된 토큰이 없습니다.");
    }
}