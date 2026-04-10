package com.project.iob.post.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.post.dto.MyPostRequestDto;
import com.project.iob.post.dto.MyPostResponseDto;
import com.project.iob.post.dto.PostRequestDto;
import com.project.iob.post.dto.PostResponseDto;
import com.project.iob.post.service.PostService;
import com.project.iob.post.service.repository.mybatis.PostDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
	private final PostDAO postDAO;
	/**
     * [게시글 목록]
     */
	@Override
	public List<PostResponseDto> searchPosts(PostRequestDto postRequestDto) {
		return postDAO.find(postRequestDto);
	}
	
	@Override
	public int searchPostCount(PostRequestDto postRequestDto) {
		return postDAO.findPostCount(postRequestDto);
	}

	@Override
	public void updatePostDelYn(PostRequestDto postRequestDto) {
		postDAO.updatePostDelYn(postRequestDto);
		postDAO.updatePostCommentDelYn(postRequestDto);
	}
	
	/**
     * [사용자별 게시글 목록]
     */
	@Override
	public List<MyPostResponseDto> searchMyPosts(MyPostRequestDto myPostRequestDto) {
	    return postDAO.findByUser(myPostRequestDto);
	}
	
	/**
     * [사용자별 게시글 수]
     */
	@Override
	public int searchMyPostCount(MyPostRequestDto myPostRequestDto) {
	    return postDAO.findByUserCount(myPostRequestDto);
	}
	
	

}