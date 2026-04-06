package com.project.iob.post.dto;

import java.sql.Date;

public record PostResponseDto(
        Long postId,
        String userEmail,
        String categoryCode,
        String title,
        Date createdAt,
        Date updatedAt,
        Date deletedAt,
        String nickname,
        Integer likes,
        Integer comments,
        String thumbnail,
        String delYn
) {
}