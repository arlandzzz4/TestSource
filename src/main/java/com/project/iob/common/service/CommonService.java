package com.project.iob.common.service;

import java.util.List;

import com.project.iob.common.dto.CodeDto;
import com.project.iob.common.dto.CodeGroupsDto;

public interface CommonService {
	List<CodeGroupsDto> getCodeGroupList(String code);

	List<CodeDto> getCodeList();
}
