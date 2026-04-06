package com.project.iob.post.service.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.post.dto.CommentRequestDto;

@Mapper
public interface CommentDAO {

	int findCommentCount(@Param("postId") String postId, @Param("delYn") String delYn, @Param("today") String today);

	void updateCommentDelYn(CommentRequestDto commentRequestDto);
    
}