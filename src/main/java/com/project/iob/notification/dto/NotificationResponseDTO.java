package com.project.iob.notification.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationResponseDTO {
    private Long notiId;          // noti_id → notiId
    private String notiType;      // noti_type → notiType
    private String senderEmail;   // sender_email → senderEmail
    private String message;
    private Long targetId;        // target_id → targetId
    private String readYn;        // read_yn → readYn
    private LocalDateTime createdAt;  // created_at → createdAt
}