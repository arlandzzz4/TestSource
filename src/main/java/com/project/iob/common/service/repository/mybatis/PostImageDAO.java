package com.project.iob.common.service.repository.mybatis;
 
import java.util.List;
 
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
 
@Mapper
public interface PostImageDAO {
    void insertImages(@Param("postId") Long postId,
                      @Param("imageUrls") List<String> imageUrls);
}
 