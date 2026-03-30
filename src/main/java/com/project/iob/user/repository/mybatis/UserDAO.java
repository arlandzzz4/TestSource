package com.project.iob.user.repository.mybatis;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.project.iob.user.entity.User;

import jakarta.validation.constraints.NotBlank;

@Mapper
public interface UserDAO {
    // 이메일로 사용자 상세 정보 조회
    Optional<User> findByEmail(String email);

	void updateFcmToken(String email, @NotBlank(message = "fcmToken은 필수 입력 값입니다.") String fcmToken);

	void clearFcmToken(String email);
    
    // 특정 조건의 사용자 리스트 조회 (예시)
    // List<Users> findAllActiveUsers();
}