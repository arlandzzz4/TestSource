package com.project.iob.post.dto;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentRequestDto(
		Long commentId,
        Long postId,
        String userEmail,
        Long parent_comment_id,
        String content,
        Date createdAt,
        Date updatedAt,
        Date deleteAt,
        String delYn,
        @Schema(description = "마지막 조회 ID", defaultValue = "0")
        Long lastId,
        @Schema(description = "페이지 크기", defaultValue = "10")
        Integer size
) {
	public CommentRequestDto {
        if (lastId == null) lastId = 0L;
        if (size == null) size = 10;
    }
}