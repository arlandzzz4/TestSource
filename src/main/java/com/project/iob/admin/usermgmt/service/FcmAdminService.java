package com.project.iob.admin.usermgmt.service;

public interface FcmAdminService {

    /**
     * 특정 토큰을 가진 디바이스로 알림 전송
     * @param token 유저의 FCM 토큰 (DB에서 가져옴)
     * @param title 알림 제목
     * @param body 알림 내용
     * @param path 클릭 시 이동할 상세 경로 (예: /challenge/1)
     */
    public void sendMessage(String token, String title, String body, String path);

}