package com.project.iob.post.service.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.iob.post.dto.PostRequestDto;
import com.project.iob.post.dto.PostResponseDto;

@Mapper
public interface PostDAO {
    // 이메일로 사용자 상세 정보 조회
    List<PostResponseDto> find(PostRequestDto postRequestDto);

	int findPostCount(PostRequestDto postRequestDto);

	void updatePostDelYn(PostRequestDto postRequestDto);
    
}