package com.project.iob.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class RefreshToken {

    @Id
    @Column(name = "email")
    private String email;

    @Column(nullable = true, name = "refresh_token")
    private String refreshToken;

    @Column(nullable = true)
    private String password;

    @Column(name = "token_rotated_at")
    private LocalDateTime tokenRotatedAt;

    @Column(name = "provider_code", nullable = true)
    private String providerCode = "01";

    @Column(nullable = true, name = "provider_id")
    private String providerId;

    public void updateToken(String newToken) {
        this.refreshToken = newToken;
        this.tokenRotatedAt = LocalDateTime.now();
    }

    @Builder
    public RefreshToken(String email, String token, String password) {
        this.email = email;
        this.password = password;
        this.refreshToken = token;
        this.tokenRotatedAt = LocalDateTime.now();
    }
}