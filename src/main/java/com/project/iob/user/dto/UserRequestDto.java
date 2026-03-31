package com.project.iob.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
    @Email(message = "이메일 형식에 맞지 않습니다.")
    String email,
    
    String nickname,

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    String password,
    
    @NotBlank(message = "소셜 연동 필드 값은 필수 입력 값입니다.")
    String providerCode,
    
    String providerId,
    
    String termsAgreedYn,
    
    String privacyAgreedYn
) {}