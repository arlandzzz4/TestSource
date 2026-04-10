package com.project.iob.admin.usermgmt.service;

import com.google.firebase.messaging.Notification;

public interface FcmAdminService {

	/**
	 * 특정 토큰을 가진 디바이스로 알림 전송
	 * 
	 * @param token        유저의 FCM 토큰 (DB에서 가져옴)
	 * @param url          클릭 시 이동할 상세 경로 (예: /challenge/1)
	 * @param notification title, body가 들어 있음
	 */
	public void sendMessage(String token, String url, Notification notification);

	public void sendNoticeNotifications(Long postId, String title, String content);
	
	// 게시글 상세 페이지 링크 생성
	public String createPostLink(Long postId, String path);

}