package com.project.iob.report.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.report.dto.ReportRequestDto;
import com.project.iob.report.dto.ReportResponseDto;
import com.project.iob.report.repository.mybatis.ReportDAO;
import com.project.iob.report.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {
    private final ReportDAO reportDAO; // MyBatis 매퍼 주입
	
	/**
     * [신고수 조회]
     */
	@Override
	public int searchReportCount(String targetCode, String today) {
		return reportDAO.findReportCount(targetCode, today);
	}
	/**
     * [신고 조회]
     */
	@Override
	public List<ReportResponseDto> searchReports(ReportRequestDto reportRequestDto) {
		return reportDAO.findReportList(reportRequestDto);
	}
}