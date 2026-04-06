package com.project.iob.post.service.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;

@Mapper
public interface CommentDAO {

	int findCommentCount(CommentRequestDto commentRequestDto);

	void updateCommentDelYn(CommentRequestDto commentRequestDto);

	List<CommentResponseDto> findComments(CommentRequestDto commentRequestDto);
    
}