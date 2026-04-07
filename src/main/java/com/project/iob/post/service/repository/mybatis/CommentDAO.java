package com.project.iob.post.service.repository.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;

@Mapper
public interface CommentDAO {
    
    int findCommentCount(CommentRequestDto commentRequestDto);
    void updateCommentDelYn(CommentRequestDto commentRequestDto);
    List<CommentResponseDto> findComments(CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getCommentList(@Param("postId") Long postId);
    void insertComment(CommentRequestDto commentRequestDto);
    void deleteComment(@Param("commentId") Long commentId);
    void insertCommentLike(@Param("commentId") Long commentId, @Param("userEmail") String userEmail);
    void deleteCommentLike(@Param("commentId") Long commentId, @Param("userEmail") String userEmail);
    int checkCommentLike(@Param("commentId") Long commentId, @Param("userEmail") String userEmail);
}