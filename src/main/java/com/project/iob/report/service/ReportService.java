package com.project.iob.report.service;

import java.util.List;

import com.project.iob.report.dto.ReportRequestDto;
import com.project.iob.report.dto.ReportResponseDto;

public interface ReportService {
	
	
	/**
     * [리포트 수 조회]
     */
	public int searchReportCount(String targetCode, String today);
	/**
     * [리포트 조회]
     */
	public List<ReportResponseDto> searchReports(ReportRequestDto reportRequestDto);
	/**
     * [신고 상태 코드 수정]
     */
	public void updateReportStatusCode(ReportRequestDto reportRequestDto);

}