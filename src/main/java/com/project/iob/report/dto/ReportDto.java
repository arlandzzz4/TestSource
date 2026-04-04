package com.project.iob.report.dto;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReportDto(
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
        @Schema(description = "마지막 조회 ID", defaultValue = "0")
        Long lastId,
        @Schema(description = "페이지 크기", defaultValue = "10")
        Integer size
) {
}