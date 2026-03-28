package com.project.iob.admin.usermgmt.controller;

//1. Spring Framework 관련 (HTTP 요청/응답 처리)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.Notification;
//3. 프로젝트 내부 DTO 및 Service (리더님의 패키지 경로에 맞게 확인)
import com.project.iob.admin.usermgmt.dto.FcmRequestDto;
import com.project.iob.admin.usermgmt.service.FcmAdminService;
//4. 프로젝트 내부 Entity 및 Repository
import com.project.iob.user.entity.User;
import com.project.iob.user.querydsl.UserRepository;

//5. 유틸리티 및 어노테이션
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Fcm Admin API", description = "Fcm Admin API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/fcm")
public class FcmAdminController {

    private final FcmAdminService fcmAdminService;
    private final UserRepository userRepository; // 유저 토큰 조회를 위해 필요
    //private final NotificationRepository notificationRepository; // 추가: 내역 저장을 위해 필요

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody FcmRequestDto request) {
        // 1. 대상 유저 조회
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String token = user.getFcmToken();
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("유저에게 등록된 토큰이 없습니다.");
        }

        try {
            // 2. 알림 내역 DB 저장 (기록용)
        	/* TODO
            NotificationHistory history = NotificationHistory.builder()
                    .user(user)
                    .title(request.title())
                    .body(request.body())
                    .path(request.path())
                    .isRead(false)
                    .build();
            notificationRepository.save(history);
            */
            
            // 3. FCM 메시지 객체 조립 (표준 규격 준수)
            Notification notification = Notification.builder()
                    .setTitle(request.title())
                    .setBody(request.body())
                    .build();

            // 4. 실제 FCM 발송 호출
            // 변경 이유: 서비스 레이어에서 복잡한 조립 로직을 분리하고, 컨트롤러에서 메시지 규격을 제어하기 위함입니다.
            fcmAdminService.sendMessage(token, request.path(), notification);

            return ResponseEntity.ok("전송 및 내역 저장 성공");
        } catch (Exception e) {
            log.error("FCM 발송 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("발송 실패: " + e.getMessage());
        }
    }
}