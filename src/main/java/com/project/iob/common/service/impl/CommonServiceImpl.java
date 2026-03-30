package com.project.iob.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.common.dto.CodeDto;
import com.project.iob.common.dto.CodeGroupsDto;
import com.project.iob.common.service.CommonService;
import com.project.iob.common.service.repository.mybatis.CommonDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommonServiceImpl implements CommonService {
	private final CommonDAO commonDAO;

	@Override
	public List<CodeGroupsDto> getCodeGroupList(String code) {
		List<CodeGroupsDto> list = commonDAO.findCodeGroupList(code);
		return list;
	}

	@Override
	public List<CodeDto> getCodeList() {
		List<CodeDto> list = commonDAO.findCodeList();
		return list;
	}

}
