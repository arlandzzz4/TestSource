package com.project.iob.admin.usermgmt.service.impl;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.project.iob.admin.usermgmt.service.FcmAdminService;

@Service
public class FcmAdminServiceImpl implements FcmAdminService {

    /**
     * 특정 토큰을 가진 디바이스로 알림 전송
     * @param token 유저의 FCM 토큰 (DB에서 가져옴)
     * @param title 알림 제목
     * @param body 알림 내용
     * @param path 클릭 시 이동할 상세 경로 (예: /challenge/1)
     */
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

    private void handleFcmException(FirebaseMessagingException e, String token) {
        if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED 
            || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
            System.err.println("유효하지 않은 토큰 발견. DB에서 삭제 처리가 필요합니다: " + token);
            // 여기서 userService.deleteFcmToken(token) 호출 로직 추가
        }
    }
}