package com.project.iob.report.dto;

import java.sql.Date;

public record ReportResponseDto(
        Long reportId,
        String targetCode,
        Long targetId,
        String reporterEmail,
        String reasonCode,
        String detail,
        String reportStatusCode,
        String processedBy,
        Date createdAt,
        Date processedAt,
        String targetNickname,
        String reporterNickname,
        String content
) {
}