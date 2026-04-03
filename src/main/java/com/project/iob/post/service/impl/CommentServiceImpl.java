package com.project.iob.post.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.post.service.CommentService;
import com.project.iob.post.service.repository.mybatis.CommentDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
	private final CommentDAO commentDAO;
	/**
     * [댓글 수]
     */
	@Override
	public int searcCommentCount(String today) {
		return commentDAO.findCommentCount(null, today);
	}
	

}