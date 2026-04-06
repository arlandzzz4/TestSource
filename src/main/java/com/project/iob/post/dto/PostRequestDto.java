package com.project.iob.post.dto;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostRequestDto(
        Long postId,
        String userEmail,
        String categoryCode,
        String title,
        Date createdAt,
        Date updatedAt,
        String nickname,
        Integer likes,
        Integer comments,
        String thumbnail,  // 추가
        String delYn,
        String deletedId,
        @Schema(description = "마지막 조회 ID", defaultValue = "0")
        Long lastId,
        @Schema(description = "페이지 크기", defaultValue = "10")
        Integer size,
        Long reportId,
        Integer offset,
        String today
) {
	public PostRequestDto {
        if (lastId == null) lastId = 0L;
        if (size == null) size = 10;
        if (likes == null) likes = 0;
        if (comments == null) comments = 0;
    }
	
	public PostRequestDto(String today) {
		this(null, null, null, null, null, null, null, 0, 0, null, null, null, 0L, 10, null, null, today);
	}
}