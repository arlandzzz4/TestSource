package com.project.iob.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserAuthRequestDto(
		String email,
	    
	    String nickname,

	    String termsAgreedYn,
	    
	    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	    String password,
	    
	    @NotBlank(message = "소셜 연동 필드 값은 필수 입력 값입니다.")
	    String providerCode,
	    
	    String providerId,
	    
	    String privacyAgreedYn,
	    @Schema(description = "마지막 조회 ID", defaultValue = "0")
	    Long lastId,
	    @Schema(description = "페이지 크기", defaultValue = "10")
	    Integer size
	) {
		public UserAuthRequestDto {
	        if (lastId == null) lastId = 0L;
	        if (size == null) size = 10;
	    }
	}