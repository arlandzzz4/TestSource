package com.project.iob.user.service;

import java.util.List;

import com.project.iob.user.dto.UserAuthRequestDto;
import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.dto.UserResponseDto;
import com.project.iob.user.entity.User;

import jakarta.validation.constraints.NotBlank;

public interface UserService {
	/**
     * [회원가입 로직]
     */
	public User regist(UserAuthRequestDto UserRequest);
	
	/**
     * [회원 정보 조회 로직]
     */
	public User searchUserByEmail(String email);
	
	/**
     * [FCM 토큰 업데이트 로직]
     */
	public void updateFcmToken(String email, @NotBlank(message = "fcmToken은 필수 입력 값입니다.") String fcmToken);

	/**
     * [FCM 토큰 클리어 로직]
     */
	public void clearFcmToken(String email);
	
	/**
     * [유저 수 조회]
     */
	public int searchUserCount(UserRequestDto userRequestDto);
	/**
     * [유저 목록 조회]
     */
	public List<UserResponseDto> searchUsers(UserRequestDto userRequestDto);
	/**
     * [유저 상태 수정]
     */
	public void updateUserStatusCode(UserRequestDto userRequestDto);

	public void unsubscribe(String email);

}