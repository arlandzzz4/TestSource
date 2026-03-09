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
    
    @Column(nullable = false)
    private String token;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private LocalDateTime rotatedAt;
    
    @Column(nullable = false)
    private String provider = "local";
    
    @Column(nullable = true)
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