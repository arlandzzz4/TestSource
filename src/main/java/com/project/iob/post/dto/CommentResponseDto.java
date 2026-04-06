package com.project.iob.post.dto;

import java.sql.Date;

public record CommentResponseDto(
		Long commentId,
        Long postId,
        String userEmail,
        Long parentCommentId,
        String content,
        Date createdAt,
        Date updatedAt,
        Date deleteAt,
        String delYn
) {
}