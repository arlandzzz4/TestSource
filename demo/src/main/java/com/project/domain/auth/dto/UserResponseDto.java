package com.project.domain.auth.dto;

import com.project.domain.auth.entity.Users;

import lombok.Builder;

@Builder
public record UserResponseDto(
	Long id,
	
    String email,
    
    String nickname,

    String provider,
    
    String provider_id,
    
    String createdAt
) {
	//엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static UserResponseDto from(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}