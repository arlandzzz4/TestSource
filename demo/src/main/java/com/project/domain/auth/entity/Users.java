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
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 100, unique = true)
    private String email;
    
    private String password;
    
    private String nickname;
    
    private String provider = "local";
    
    @Column(name = "provider_id")
    private String providerId;
    
    @Column(name = "refresh_token")
    private String refreshToken;
    
    @Column(name = "token_rotated_at")
    private String tokenRotatedAt;
    
    @Column(name = "created_at")
    private String createdAt;
    
    @Column(name = "updated_at")
    private String updatedAt;
    
    @Builder
    public Users(String email, String password, String nickname, String provider, String providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = LocalDateTime.now().toString(); // 생성 시 시간 자동 입력 예시
        this.updatedAt = LocalDateTime.now().toString();
    }
}