package com.project.iob.post.service.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.post.dto.PostDto;

@Mapper
public interface PostDAO {
    // 이메일로 사용자 상세 정보 조회
    List<PostDto> find(@Param("lastId") Long lastId, @Param("size") int size);
    
}