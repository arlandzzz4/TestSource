package com.project.iob.post.service.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.post.dto.PostRequestDto;
import com.project.iob.post.dto.PostResponseDto;

@Mapper
public interface PostDAO {
    // 이메일로 사용자 상세 정보 조회
    List<PostResponseDto> find(PostRequestDto postRequestDto);

	int findPostCount(@Param("categoryCode") String categoryCode, @Param("delYn") String delYn, @Param("today") String today);

	void updatePostDelYn(PostRequestDto postRequestDto);
    
}