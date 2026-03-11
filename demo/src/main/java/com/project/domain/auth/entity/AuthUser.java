package com.project.domain.auth.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class AuthUser {

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
    private LocalDateTime tokenRotatedAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Builder
    public AuthUser(String email, String password, String nickname, String provider, String providerId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = LocalDateTime.now(); // 생성 시 시간 자동 입력 예시
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 저장 시 자동으로 현재 시간 주입
        this.updatedAt = LocalDateTime.now(); // 저장 시 자동으로 현재 시간 주입
    }
    
    @PreUpdate
    public void preUpdate() {
        // 데이터가 수정될 때마다 자동으로 실행됨
        this.updatedAt = LocalDateTime.now();
    }
}