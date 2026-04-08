package com.project.iob.post.dto;

import java.sql.Timestamp;

public record CommentResponseDto(
        Long commentId,
        Long postId,
        String userEmail,
        Long parentCommentId,
        String content,
        Timestamp createdat,
        Timestamp updatedat,
        Timestamp deleteat,
        String delYn,
        String nickname,
        String title,
        int likeCount,
        int isLiked
) {
}