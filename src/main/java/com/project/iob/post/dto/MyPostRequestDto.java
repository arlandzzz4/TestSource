package com.project.iob.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyPostRequestDto(
    @Schema(description = "사용자 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    String userEmail,
    @Schema(description = "페이지 오프셋", defaultValue = "0")
    Integer offset,
    @Schema(description = "페이지 크기", defaultValue = "10")
    Integer size
) {
    public MyPostRequestDto {
        if (offset == null) offset = 0;
        if (size == null) size = 10;
    }
}