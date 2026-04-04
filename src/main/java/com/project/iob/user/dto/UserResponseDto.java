package com.project.iob.user.dto;

import java.sql.Date;

public record UserResponseDto(
    String email,
    
    String nickname,

    String providerCode,
    
    String profileImageUrl,
    
    String userStatusCode,
    
    String roleCode,
    
    String termsAgreedYn,
    
    String privacyAgreedYn,
    
    Date createdAt,
    Date updatedAt,
    Date deletedAt,
    Date lastLoginAt
) {
}