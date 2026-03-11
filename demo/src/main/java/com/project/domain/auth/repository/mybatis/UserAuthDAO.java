package com.project.domain.auth.repository.mybatis;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.project.domain.auth.entity.AuthUser;

@Mapper
public interface UserAuthDAO {
    // 이메일로 사용자 상세 정보 조회
    Optional<AuthUser> findByEmail(String email);
    
    // 특정 조건의 사용자 리스트 조회 (예시)
    // List<Users> findAllActiveUsers();
}