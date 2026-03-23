package com.project.iob.user.service;

import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.entity.User;

import jakarta.validation.constraints.NotBlank;

public interface UserService {
	/**
     * [회원가입 로직]
     */
	public User regist(UserRequestDto UserRequest);
	
	/**
     * [회원 정보 조회 로직]
     */
	public User searchUserByEmail(String email);
	
	/**
     * [FCM 토큰 업데이트 로직]
     */
	public void updateFcmToken(Long id, @NotBlank(message = "fcmToken은 필수 입력 값입니다.") String fcmToken);

}