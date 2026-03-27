package com.project.iob.post.dto;

import java.sql.Date;

public record PostDto(
		Long postId, 
		String userEmail, 
		int categoryId, 
		String title, 
		Date createdAt, 
		Date updatedAt, 
		String nickname, 
		Long likes, 
		Long comments
		) 
{
}