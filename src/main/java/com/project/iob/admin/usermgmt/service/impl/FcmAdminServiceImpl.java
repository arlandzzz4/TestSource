package com.project.iob.admin.usermgmt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.project.iob.admin.usermgmt.dto.FcmUserDto;
import com.project.iob.admin.usermgmt.service.FcmAdminService;
import com.project.iob.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmAdminServiceImpl implements FcmAdminService {
	
	private final NotificationService notificationService;
	
	@Value("${app.client-url}")
    private String clientUrl;

    /**
     * 특정 토큰을 가진 디바이스로 알림 전송
     * @param token 유저의 FCM 토큰 (DB에서 가져옴)
     * @param title 알림 제목
     * @param body 알림 내용
     * @param path 클릭 시 이동할 상세 경로 (예: /challenge/1)
     */
	@Override
    public void sendMessage(String token, String url, Notification notification) {
        // 1. 메시지 구성 (Notification + Data)
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putData("link_url", url) // 핵심: 프론트엔드 sw.js와 매칭
                .build();

        // 2. 전송 요청
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            // 리더님의 지시사항: 전송 실패 시 토큰 관리 로직 포함
            handleFcmException(e, token);
        }
    }
    
	@Override
    @Async("notificationExecutor")
    public void sendNoticeNotifications(Long postId, String title, String content) {
        int pageSize = 100;
        int pageNumber = 0;
        int totalSentInBatch = 0;
        String url = createPostLink(postId);
        Notification notification = Notification.builder()
				.setTitle(title)
				.setBody(content)
				.build();
        Page<FcmUserDto> userPage;
        do {
            userPage = notificationService.findAllByNotificationEnabledTrue(PageRequest.of(pageNumber, pageSize));
            
            for (FcmUserDto user : userPage.getContent()) {
                this.sendMessage(user.getFcmToken(), url, notification);
                totalSentInBatch++;

                // 2,000건을 채웠을 때
                if (totalSentInBatch >= 2000) {
                    log.info("2,000건 전송 완료. 1시간 휴식에 들어갑니다.");
                    try {
                        // 1시간(3,600,000ms) 동안 현재 스레드를 재웁니다.
                        Thread.sleep(3600000); 
                    } catch (InterruptedException e) {
                        log.error("휴식 중 인터럽트 발생", e);
                        Thread.currentThread().interrupt();
                        return;
                    }
                    totalSentInBatch = 0; // 카운트 초기화
                }
            }
            
            pageNumber++;
            // 중요: 각 페이지 처리 사이에 짧은 휴식(Throttle)을 주어 부하 조절 가능
        } while (userPage.hasNext());
        
        log.info("모든 알림 전송 완료!");
    }

    private void handleFcmException(FirebaseMessagingException e, String token) {
        if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED 
            || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
            System.err.println("유효하지 않은 토큰 발견. DB에서 삭제 처리가 필요합니다: " + token);
            // 여기서 userService.deleteFcmToken(token) 호출 로직 추가
        }
    }
    
    public String createPostLink(Long postId) {
        return UriComponentsBuilder.fromUriString(clientUrl)
                .path("/posts/{id}")
                .buildAndExpand(postId)
                .toUriString();
    }
}