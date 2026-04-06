package com.project.iob.user.repository.mybatis;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.dto.UserResponseDto;
import com.project.iob.user.entity.User;

@Mapper
public interface UserDAO {
    // 이메일로 사용자 상세 정보 조회
    Optional<User> findByEmail(String email);

	void updateFcmToken(@Param("email")String email, @Param("fcmToken") String fcmToken);

	void clearFcmToken(String email);

	int findUserCount(UserRequestDto userRequestDto);

	List<UserResponseDto> findUserList(UserRequestDto userRequestDto);

	void updateUserStatusCode(UserRequestDto userRequestDto);

    // 특정 조건의 사용자 리스트 조회 (예시)
    // List<Users> findAllActiveUsers();
}

