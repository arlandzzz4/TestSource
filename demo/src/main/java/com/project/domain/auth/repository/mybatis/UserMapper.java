package com.project.domain.auth.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import com.project.domain.auth.entity.Users;
import java.util.Optional;

@Mapper
public interface UserMapper {
    // 이메일로 사용자 상세 정보 조회
    Optional<Users> findByEmail(String email);
    
    // 특정 조건의 사용자 리스트 조회 (예시)
    // List<Users> findAllActiveUsers();
}