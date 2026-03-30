package com.project.iob.common.dto;

import java.sql.Date;

public record CodeGroupsDto(
		String groupCode, 
		String groupName, 
		String description,
		String useYn, 
		int sortOrder, 
		Date createdAt, 
		String createdId, 
		Date updatedAt, 
		String updatedId
		) 
{
}