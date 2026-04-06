package com.project.iob.post.service;

import java.util.List;

import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;

public interface CommentService {
	/**
     * [댓글 수 조회]
     */
	public int searchCommentCount(CommentRequestDto commentRequestDto);

	public void updateCommentDelYn(CommentRequestDto commentRequestDto);

	public List<CommentResponseDto> searchComments(CommentRequestDto commentRequestDto);
	

}