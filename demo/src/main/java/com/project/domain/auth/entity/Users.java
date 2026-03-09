package com.project.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

    
}