package com.project.iob.report.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.report.dto.ReportRequestDto;
import com.project.iob.report.dto.ReportResponseDto;

@Mapper
public interface ReportDAO {

	int findReportCount(@Param("targetCode")String targetCode, @Param("today")String today, @Param("reportStatusCode")String reportStatusCode);
	
	List<ReportResponseDto> findReportList(ReportRequestDto reportRequestDto);

	void updateReportStatusCode(ReportRequestDto reportRequestDto);
    
}

