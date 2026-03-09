package com.project.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {

    @Id
    private String id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = true)
    private String password;
    
    @Column(nullable = false)
    private String nickname;
    
    @Column(nullable = false)
    private String provider;
    
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

    
}