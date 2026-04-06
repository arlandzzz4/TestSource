package com.project.iob.post.service.repository.mybatis;
 
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.iob.post.dto.PostWriteDto;
 
@Mapper
public interface PostWriteDAO {
    // 게시글 등록
    void insert(@Param("dto") PostWriteDto dto);
}
 