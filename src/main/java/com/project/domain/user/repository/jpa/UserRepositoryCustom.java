package com.project.domain.user.repository.jpa;

import org.springframework.stereotype.Repository;

import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Repository
@Table(name = "USERS")
public interface UserRepositoryCustom {
    boolean existsByEmail(@Email(message = "이메일 형식에 맞지 않습니다.") String email);
    boolean existsByProviderId(String providerId);
}
