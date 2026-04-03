package com.project.iob.post.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.post.dto.PostDto;
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
	public List<PostDto> searchPosts(Long lastId, int size){
		List<PostDto> list = postDAO.find(lastId, size);
		return list;
	}
	@Override
	public int searchPostCount(String categoryCode, String delYn, String today) {
		return postDAO.findPostCount(categoryCode, delYn,today);
	}
	

}