package com.project.domain.auth.repository.jpa;

import org.springframework.stereotype.Repository;

import jakarta.persistence.Table;

@Repository
@Table(name = "USERS")
public interface UserRepositoryCustom {
    boolean existsByEmail(String email, String provider, String password);
}
