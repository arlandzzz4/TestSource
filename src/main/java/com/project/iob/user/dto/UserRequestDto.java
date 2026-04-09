package com.project.iob.user.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequestDto(
    String email,
    
    String nickname,

    String providerCode,
    
    String termsAgreedYn,
    
    String userStatusCode,
    
    @Schema(description = "USER_ROLE", defaultValue = "01")
    String roleCode,
    
    String oneMonthAgo, 
    String today,
    
    String privacyAgreedYn,
    @Schema(description = "마지막 조회 ID")
    LocalDateTime lastId,
    @Schema(description = "페이지 크기", defaultValue = "10")
    Integer size,
    Integer offset
) {
	public UserRequestDto {
        if (size == null) size = 10;
        if (roleCode == null) roleCode = "01";
    }
	
	public UserRequestDto(String email, String nickname, String providerCode, String termsAgreedYn, String userStatusCode, String roleCode, String oneMonthAgo, String today, String privacyAgreedYn) {
		this(email, nickname, providerCode, termsAgreedYn, userStatusCode, roleCode, oneMonthAgo, today, privacyAgreedYn, null, 10, null);
	}
	
	public UserRequestDto(String oneMonthAgo, String today) {
		this(null, null, null, null, null, "01", oneMonthAgo, today, null, null, 10, null);
	}
}