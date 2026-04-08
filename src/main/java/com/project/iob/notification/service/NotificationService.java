package com.project.iob.notification.service;

import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.dto.NotificationResponseDTO;
import java.util.List;

public interface NotificationService {

    // 알림 목록 조회
    List<NotificationResponseDTO> getNotifications(String userEmail);

    // 단일 알림 읽음 처리
    void markAsRead(Long notiId);

    // 전체 알림 읽음 처리
    void markAllRead(String userEmail);

    // 전체 알림 삭제
    void deleteAll(String userEmail);

    // 알림 생성
    void createNotification(NotificationDTO.CreateRequest request);
}