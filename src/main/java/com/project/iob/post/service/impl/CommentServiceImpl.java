package com.project.iob.post.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;
import com.project.iob.post.service.CommentService;
import com.project.iob.post.service.repository.mybatis.CommentDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    /**
     * [댓글 수]
     */
    
    @Override
    public int searchCommentCount(CommentRequestDto commentRequestDto) {
        return commentDAO.findCommentCount(commentRequestDto);
    }

    @Override
    public void updateCommentDelYn(CommentRequestDto commentRequestDto) {
        commentDAO.updateCommentDelYn(commentRequestDto);
    }

    @Override
    public List<CommentResponseDto> searchComments(CommentRequestDto commentRequestDto) {
        return commentDAO.findComments(commentRequestDto);
    }

    @Override
    public List<CommentResponseDto> getCommentList(Long postId, String userEmail) {
        return commentDAO.getCommentList(postId, userEmail);
    }

    @Override
    public void insertComment(CommentRequestDto commentRequestDto) {
        commentDAO.insertComment(commentRequestDto);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentDAO.deleteComment(commentId);
    }

    @Override
    public boolean toggleCommentLike(Long commentId, String userEmail) {
        int count = commentDAO.checkCommentLike(commentId, userEmail);
        if (count > 0) {
            commentDAO.deleteCommentLike(commentId, userEmail);
            return false;
        } else {
            commentDAO.insertCommentLike(commentId, userEmail);
            return true;
        }
    }
}