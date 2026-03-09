package com.project.domain.auth.repository.jpa;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.domain.auth.entity.RefreshToken;

import jakarta.persistence.Table;

@Repository
@Table(name = "USERS")
public interface RefreshTokenRepositoryCustom {
	Optional<RefreshToken> findByEmail(String email, String provider, String password);
    Optional<RefreshToken> findByEmail(String email);
    Optional<RefreshToken> findByToken(String token);
    void deleteRefreshTokenById(String email, String provider);
}
