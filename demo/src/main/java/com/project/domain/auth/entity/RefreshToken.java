package com.project.domain.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String email;
    
    @Column(nullable = false)
    private String token;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private LocalDateTime rotatedAt;
    
    @Column(nullable = false)
    private String provider;
    
    @Column(nullable = false)
    private String providerId;

    // 토큰 값 업데이트 (로그인 시마다 갱신)
    public void updateToken(String newToken) {
        this.token = newToken;
        this.rotatedAt = LocalDateTime.now();
    }
    
    @Builder
    public RefreshToken(String email, String token, String password) {
        this.email = email;
        this.password = password;
        this.token = token;
        this.rotatedAt = LocalDateTime.now();
    }
}