package com.project.iob.post.dto;

import java.sql.Date;

public record PostDto(
        Long postId,
        String userEmail,
        String categoryCode,
        String title,
        Date createdAt,
        Date updatedAt,
        String nickname,
        int likes,
        int comments,
        String thumbnail  // 추가
) {
}