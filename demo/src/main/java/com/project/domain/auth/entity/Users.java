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
    
    @Column(nullable = true)
    private String email;
    
    @Column(nullable = true)
    private String password;
    
    @Column(nullable = false)
    private String nickname;
    
    @Column(nullable = false)
    private String provider = "local";
    
    @Column(nullable = false)
    private String providerId;
    
    @Column(nullable = false)
    private String refreshToken;
    
    @Column(nullable = false)
    private String tokenRotatedAt;
    
    @Column(nullable = false)
    private String createdAt;
    
    @Column(nullable = false)
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