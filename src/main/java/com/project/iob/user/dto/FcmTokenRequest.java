package com.project.iob.user.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
		@NotBlank(message = "fcmToken은 필수 입력 값입니다.")
	    String fcmToken
	) {}