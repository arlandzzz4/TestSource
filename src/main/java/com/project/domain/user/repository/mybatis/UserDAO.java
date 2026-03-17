package com.project.domain.user.repository.mybatis;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.project.domain.user.entity.User;

@Mapper
public interface UserDAO {
    // 이메일로 사용자 상세 정보 조회
    Optional<User> findByEmail(String email);
    
    // 특정 조건의 사용자 리스트 조회 (예시)
    // List<Users> findAllActiveUsers();
}