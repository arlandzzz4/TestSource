package com.project.iob.report.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportDAO {

	int findReportCount(@Param("today")String today);
    
}

