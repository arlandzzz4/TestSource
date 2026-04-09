package com.project.iob.post.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long commentId,
        Long postId,
        String userEmail,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        String delYn,
        String nickname,
        String title,
        int likeCount,
        int isLiked
) {
}