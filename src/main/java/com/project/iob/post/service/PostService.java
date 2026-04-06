package com.project.iob.post.service;

import java.util.List;

import com.project.iob.post.dto.PostRequestDto;
import com.project.iob.post.dto.PostResponseDto;

public interface PostService {
	/**
     * [게시글 조회]
     */
	public List<PostResponseDto> searchPosts(PostRequestDto postRequestDto);
	/**
     * [게시글 수 조회]
     */
	public int searchPostCount(PostRequestDto postRequestDto);
	/**
     * [게시글 삭제 조회]
     */
	public void updatePostDelYn(PostRequestDto postRequestDto);
	
	

}