package com.project.iob.photo.service.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PhotoDAO {
    // 이미지 등록
    void insertImages(@Param("postId") Long postId,
                      @Param("imageUrls") List<String> imageUrls);
    
    // 이미지 삭제
    void deleteImage(@Param("fileName") String fileName);
}

