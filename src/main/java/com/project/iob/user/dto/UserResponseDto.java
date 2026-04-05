package com.project.iob.user.dto;

import java.time.LocalDateTime;

import com.project.iob.user.entity.User;

public record UserResponseDto(
    String email,
    
    String nickname,

    String providerCode,
    
    String profileImageUrl,
    
    String userStatusCode,
    
    String roleCode,
    
    String termsAgreedYn,
    
    String privacyAgreedYn,
    
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    LocalDateTime lastLoginAt
) {

	public static UserResponseDto from(User user) {
        if (user == null) return null;

        return new UserResponseDto(
            user.getEmail(),
            user.getNickname(),
            user.getProviderCode(),
            user.getProfileImageUrl(),
            user.getUserStatusCode(),
            user.getRoleCode(),
            user.getTermsAgreedYn(),
            user.getPrivacyAgreedYn(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getDeletedAt(),
            user.getLastLoginAt()
        );
    }
}