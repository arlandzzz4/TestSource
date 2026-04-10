package com.project.iob.report.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReportRequestDto(
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
        @Schema(description = "마지막 조회 ID", defaultValue = "0")
        Long lastId,
        @Schema(description = "페이지 크기", defaultValue = "10")
        Integer size,
        Integer offset
) {
	public ReportRequestDto {
        if (lastId == null) lastId = 0L;
        if (size == null) size = 10;
        //if (reportStatusCode == null) reportStatusCode = "01";
    }

	public ReportRequestDto(Long reportId, String reportStatusCode) {
		this(reportId, null, null, null, null, null, reportStatusCode, null, null, null, null, null, null);
	}
}