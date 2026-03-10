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
//@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = true)
    private String email;
    
    @Column(nullable = true)
    private String password;
    
    @Column(nullable = true)
    private String nickname;
    
    @Column(nullable = true)
    private String provider = "local";
    
    @Column(nullable = true, name = "provider_id")
    private String providerId;
    
    @Column(nullable = true)
    private String refreshToken;
    
    @Column(nullable = true, name = "tokenRotated_at")
    private String tokenRotatedAt;
    
    @Column(nullable = true, name = "created_at")
    private String createdAt;
    
    @Column(nullable = true, name = "updated_at")
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