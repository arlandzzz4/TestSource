package com.project.iob.notification.service;

import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.dto.NotificationResponseDTO;
import java.util.List;
import java.util.Map;

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

	// 알림 설정 조회
	Map<String, String> getAllSettings(String userEmail);

	// 알림 설정 수정
	void updateNotificationSetting(String userEmail, String likeYn, String commentYn, String noticeYn);
	
	// 댓글 알림 수신 설정 조회 (Y: 알림 받음, N: 알림 안 받음)
	String getCommentYn(String userEmail);
	
	// 좋아요 알림 수신 설정 조회 (Y: 알림 받음, N: 알림 안 받음)
	String getLikeYn(String userEmail);
}