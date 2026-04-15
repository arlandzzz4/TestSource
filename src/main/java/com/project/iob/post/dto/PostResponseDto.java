package com.project.iob.post.dto;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long postId,
        String userEmail,
        String categoryCode,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        String nickname,
        Integer likes,
        Integer comments,
        String thumbnail,
        String delYn,
        int reportCnt
) {
}