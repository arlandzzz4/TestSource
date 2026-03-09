package com.project.domain.auth.repository.querydsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.domain.auth.entity.Users;
import com.project.domain.auth.repository.jpa.UserRepositoryCustom;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>, UserRepositoryCustom {
    // 기존 메서드들은 삭제해도 Custom 인터페이스를 통해 동작합니다.
}