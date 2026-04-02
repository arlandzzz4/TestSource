package com.project.iob.user.entity;

import java.time.LocalDateTime;

import com.project.global.enums.Provider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User {

	@Id
    @Column(length = 100, unique = true)
    private String email;
    
    private String nickname;
    
    private String password;
    
    @Column(name = "provider_code")
    private String providerCode = Provider.LOCAL.getKey(); // 기본값 설정
    
    @Column(name = "provider_id")
    private String providerId;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "user_status_code")
    private String userStatusCode;
    
    @Column(name = "role_code")
    private String roleCode;
    
    @Column(name = "fcm_token")
    private String fcmToken;
    
    @Column(name = "refresh_token")
    private String refreshToken;
    
    @Column(name = "terms_agreed_yn", updatable = false)
    private String termsAgreedYn;
    
    @Column(name = "privacy_agreed_yn", updatable = false)
    private String privacyAgreedYn;
    
    @Column(name = "token_rotated_at")
    private LocalDateTime tokenRotatedAt;
    
    @Column(name = "terms_agreed_at")
    private LocalDateTime termsAgreedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    public static User createEmpty() {
        return new User();
    }
    
    @Builder
    public User(String email, String nickname, String password, String providerCode, String providerId, String profileImageUrl, String userStatusCode, String roleCode, String fcmToken, String termsAgreedYn, String privacyAgreedYn,
    		LocalDateTime termsAgreedAt, LocalDateTime createdAt, LocalDateTime deletedAt, LocalDateTime lastLoginAt) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.providerCode = providerCode;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
        this.userStatusCode = userStatusCode;
        this.roleCode = roleCode;
		this.fcmToken = fcmToken;
		this.termsAgreedYn = termsAgreedYn;
		this.privacyAgreedYn = privacyAgreedYn;
		this.termsAgreedAt = LocalDateTime.now();
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
        this.updatedAt = LocalDateTime.now();
    }
    
}