package com.project.iob.post.service;

import java.util.List;
import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;

public interface CommentService {
    
    public int searchCommentCount(CommentRequestDto commentRequestDto);
    public void updateCommentDelYn(CommentRequestDto commentRequestDto);
    public List<CommentResponseDto> searchComments(CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getCommentList(Long postId, String userEmail);
    void insertComment(CommentRequestDto commentRequestDto);
    void deleteComment(Long commentId);
    boolean toggleCommentLike(Long commentId, String userEmail);
}