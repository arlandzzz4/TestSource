package com.project.iob.report.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.report.dto.ReportDto;
import com.project.iob.report.entity.Reports;
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
	public int searchReportCount(String today) {
		return reportDAO.findReportCount(today);
	}
	/**
     * [신고 조회]
     */
	@Override
	public List<Reports> searchReports(ReportDto reportDto) {
		return reportDAO.findReportList(reportDto);
	}
}