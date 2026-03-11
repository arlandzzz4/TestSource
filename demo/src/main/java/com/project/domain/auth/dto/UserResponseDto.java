package com.project.domain.auth.dto;

import java.time.LocalDateTime;

import com.project.domain.auth.entity.AuthUser;

import lombok.Builder;

@Builder
public record UserResponseDto(
	Long id,
	
    String email,
    
    String nickname,

    String provider,
    
    String providerId,
    
    LocalDateTime createdAt
) {
	//엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static UserResponseDto from(AuthUser authUser) {
        return UserResponseDto.builder()
                .id(authUser.getId())
                .email(authUser.getEmail())
                .nickname(authUser.getNickname())
                .provider(authUser.getProvider())
                .createdAt(authUser.getCreatedAt())
                .build();
    }
}