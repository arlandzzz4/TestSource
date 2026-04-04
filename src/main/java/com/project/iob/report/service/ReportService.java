package com.project.iob.report.service;

import java.util.List;

import com.project.iob.report.dto.ReportDto;
import com.project.iob.report.entity.Reports;

public interface ReportService {
	
	
	/**
     * [리포트 수 조회]
     */
	public int searchReportCount(String today);
	/**
     * [리포트 조회]
     */
	public List<Reports> searchReports(ReportDto reportDto);

}