package com.project.iob.photo.dto;

public record PhotoDto(
    Long imageId,
    Long postId,
    String imageUrl,
    int sortOrder
) {
}
