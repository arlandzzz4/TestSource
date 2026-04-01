package com.project.iob.postwrite.service.repository.mybatis;
 
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
 
import com.project.iob.postwrite.dto.PostWriteDto;
 
@Mapper
public interface PostWriteDAO {
    // 게시글 등록
    void insert(@Param("dto") PostWriteDto dto);
}
 