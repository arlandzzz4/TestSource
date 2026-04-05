package com.project.iob.post.service;

import com.project.iob.post.dto.CommentRequestDto;

public interface CommentService {
	/**
     * [댓글 수 조회]
     */
	public int searcCommentCount(String today);

	public void updateCommentDelYn(CommentRequestDto commentRequestDto);
	

}