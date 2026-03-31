package com.project.iob.common.service.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.iob.common.dto.CodeDto;
import com.project.iob.common.dto.CodeGroupsDto;

@Mapper
public interface CommonDAO {

	public List<CodeGroupsDto> findCodeGroupList(String code) ;

	public List<CodeDto> findCodeList();
	
}
