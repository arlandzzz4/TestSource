package com.project.domain.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL의 auto_increment를 사용하도록 설정
    private Long id;
    
    @Column(nullable = true)
    private String email;
    
    @Column(nullable = true,name = "refresh_token")
    private String refreshToken;
    
    @Column(nullable = true)
    private String password;
    
    @Column(name = "token_rotated_at")
    private LocalDateTime tokenRotatedAt;
    
    @Column(nullable = true)
    private String provider = "local";
    
    @Column(nullable = true, name = "provider_id")
    private String providerId;

    // 토큰 값 업데이트 (로그인 시마다 갱신)
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