package com.project.iob.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDto(
    @Email(message = "이메일 형식에 맞지 않습니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    String password,
    
    @NotBlank(message = "소셜 연동 필드 값은 필수 입력 값입니다.")
    String providerCode,
    
    String providerId,
    
    String nickname,
    String fcmToken
) {
	public LoginRequestDto(String email, String password, String providerCode, String providerId, String nickname, String fcmToken) {
		this.email = email;
		this.password = password;
		this.providerCode = providerCode;
		this.providerId = providerId;
		this.nickname = nickname;
		this.fcmToken = fcmToken;
	}
}