package com.project.iob.post.service;

import java.util.List;

import com.project.iob.post.dto.PostDto;

public interface PostService {
	/**
     * [게시글 조회]
     */
	public List<PostDto> searchPosts(Long lastId, int size);
	/**
     * [게시글 수 조회]
     */
	public int searchPostCount(String categoryCode, String delYn, String today);
	

}