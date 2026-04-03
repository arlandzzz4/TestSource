package com.project.iob.report.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * [날짜별 사용자 조회. 날짜 없을 경우 총 사용자 조회]
     */
	@Override
	public int searchReportCount(String today) {
		return reportDAO.findReportCount(today);
	}
}