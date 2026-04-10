package com.project.iob.report.dto;

import java.time.LocalDateTime;

public record ReportResponseDto(
        Long reportId,
        String targetCode,
        Long targetId,
        String reporterEmail,
        String reasonCode,
        String detail,
        String reportStatusCode,
        String processedBy,
        LocalDateTime createdAt,
        LocalDateTime processedAt,
        String targetNickname,
        String reporterNickname,
        String content,
        String postId,
        String delYn
) {
}