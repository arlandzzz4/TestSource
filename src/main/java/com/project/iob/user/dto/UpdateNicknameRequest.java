package com.project.iob.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    String email,
    
    @NotBlank(message = "변경할 닉네임은 필수입니다.")
    String nickname
) {}