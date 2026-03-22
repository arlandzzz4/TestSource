package com.project.iob.user.service;

import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.entity.User;

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