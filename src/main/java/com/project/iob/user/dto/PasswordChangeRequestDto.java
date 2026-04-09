package com.project.iob.user.dto;

public record PasswordChangeRequestDto(
    String email,
    String newPassword
) {}