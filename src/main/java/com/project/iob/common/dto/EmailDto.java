package com.project.iob.common.dto;

public record EmailDto(
		String to, 
		String subject, 
		String text
		) 
{
}