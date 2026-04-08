package com.project.iob.post.service;

import java.util.List;

import com.project.iob.post.dto.MyPostRequestDto;
import com.project.iob.post.dto.MyPostResponseDto;
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
	
	/**
     * [사용자별 게시글 리스트 조회]
     */
	 public List<MyPostResponseDto> searchMyPosts(MyPostRequestDto myPostRequestDto);
	 
	 /**
	     * [사용자별 게시글 수 조회]
	     */
	 int searchMyPostCount(MyPostRequestDto myPostRequestDto);

}