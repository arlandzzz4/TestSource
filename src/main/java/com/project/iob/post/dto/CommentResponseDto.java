package com.project.iob.post.dto;

import java.sql.Timestamp;

public record CommentResponseDto(
        Long commentId,
        Long postId,
        String userEmail,
        Long parentCommentId,
        String content,
        Timestamp createdAt,
        Timestamp updatedAt,
        Timestamp deletedAt,
        String delYn,
        String nickname,
        String title,
        int likeCount,
        int isLiked
) {
}