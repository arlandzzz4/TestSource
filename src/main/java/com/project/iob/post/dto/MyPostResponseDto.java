package com.project.iob.post.dto;

import java.sql.Date;

public record MyPostResponseDto(
    Long postId,
    String title,
    Date createdAt
) {}