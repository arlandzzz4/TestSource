package com.project.iob.notification.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class NotificationDTO {

    // 알림 목록 조회 응답 DTO
    // 💡 MyBatis는 record 대신 getter/setter 있는 클래스가 필요해요!
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long notiId;
        private String notiType;
        private String senderEmail;
        private String message;
        private Long targetId;
        private String readYn;
        private LocalDateTime createdAt;
    }

    // 알림 생성 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String userEmail;
        private String notiType;
        private String senderEmail;
        private String message;
        private Long targetId;
    }
}