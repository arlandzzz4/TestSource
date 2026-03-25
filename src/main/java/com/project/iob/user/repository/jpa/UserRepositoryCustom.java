package com.project.iob.user.repository.jpa;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.iob.user.entity.User;

import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Repository
@Table(name = "USERS")
public interface UserRepositoryCustom {
    boolean existsByEmail(@Email(message = "이메일 형식에 맞지 않습니다.") String email);
    boolean existsByProviderId(String providerId);
    Optional<User> findByEmail(String email);
}
