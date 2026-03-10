package com.project.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenDto(
    String grantType,
    String accessToken,
    String refreshToken,
    Long accessTokenExpiresIn
) {
    // 1. 전체 필드를 받는 컴팩트 생성자 (Validation이나 기본값 설정에 사용)
    public TokenDto {
        if (grantType == null) {
            grantType = "Bearer";
        }
    }

    // 2. AT와 RT만 전달할 때 사용하는 생성자 (기존 클래스의 생성자 역할)
    public TokenDto(String accessToken, String refreshToken) {
        this("Bearer", accessToken, refreshToken, null);
    }
}