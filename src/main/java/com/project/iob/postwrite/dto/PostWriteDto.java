package com.project.iob.postwrite.dto;
 
public record PostWriteDto(
        String userEmail,
        String categoryCode,
        String title,
        String content
) {
}
 