package com.project.iob.user.dto;

public record UnsubscribeRequestDto(
	    String email,
	    String providerCode,
	    String providerId,
	    String reason,
	    String currentPassword
	) {}