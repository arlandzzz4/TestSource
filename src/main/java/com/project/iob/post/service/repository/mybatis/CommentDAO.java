package com.project.iob.post.service.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentDAO {

	int findCommentCount(@Param("postId") String postId, @Param("today") String today);
    
}