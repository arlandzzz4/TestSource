package com.project.domain.user.service;

import com.project.domain.user.dto.UserRequestDto;
import com.project.domain.user.entity.User;

public interface UserService {
	/**
     * [회원가입 로직]
     */
	public User regist(UserRequestDto UserRequest);
	
	/**
     * [회원 정보 조회 로직]
     */
	public User searchUserByEmail(String email);
}