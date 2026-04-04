package com.project.iob.report.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.report.dto.ReportDto;
import com.project.iob.report.entity.Reports;

@Mapper
public interface ReportDAO {

	int findReportCount(@Param("today")String today);
	
	List<Reports> findReportList(ReportDto reportDto);
    
}

