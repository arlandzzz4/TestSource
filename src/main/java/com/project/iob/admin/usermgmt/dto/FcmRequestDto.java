package com.project.iob.admin.usermgmt.dto;

import lombok.Builder;

@Builder
public record FcmRequestDto(
    String title,
    String body,
    String path
) {
}