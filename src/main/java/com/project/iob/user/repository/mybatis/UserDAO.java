package com.project.iob.user.repository.mybatis;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.user.entity.User;

@Mapper
public interface UserDAO {
    // 이메일로 사용자 상세 정보 조회
    Optional<User> findByEmail(String email);

	void updateFcmToken(@Param("email")String email, @Param("fcmToken") String fcmToken);

	void clearFcmToken(String email);
    
    // 특정 조건의 사용자 리스트 조회 (예시)
    // List<Users> findAllActiveUsers();
}