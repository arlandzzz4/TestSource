package com.project.iob.common.dto;

import java.time.LocalDateTime;

public record CodeDto(
		String code, 
		String groupCode, 
		String groupName, 
		String description,
		String useYn, 
		int sortOrder, 
		LocalDateTime createdAt, 
		String createdId, 
		LocalDateTime updatedAt, 
		String updatedId
		) 
{
}