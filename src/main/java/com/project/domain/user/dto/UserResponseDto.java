package com.project.domain.user.dto;

import java.time.LocalDateTime;

import com.project.domain.user.entity.User;

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
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}