package com.project.iob.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDto(
    String email,
    
    String nickname,

    String providerCode,
    
    String termsAgreedYn,
    
    String privacyAgreedYn,
    @Schema(description = "마지막 조회 ID", defaultValue = "0")
    Long lastId,
    @Schema(description = "페이지 크기", defaultValue = "10")
    Integer size
) {}