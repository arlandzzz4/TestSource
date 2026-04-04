package com.project.iob.user.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequestDto(
    String email,
    
    String nickname,

    String providerCode,
    
    String termsAgreedYn,
    
    String userStatusCode,
    
    String privacyAgreedYn,
    @Schema(description = "마지막 조회 ID")
    Date lastId,
    @Schema(description = "페이지 크기", defaultValue = "10")
    Integer size,
    Integer offset
) {
	public UserRequestDto {
        if (size == null) size = 10;
    }
}