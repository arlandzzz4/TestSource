package com.project.iob.post.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.service.NotificationService;
import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;
import com.project.iob.post.service.CommentService;
import com.project.iob.post.service.repository.mybatis.CommentDAO;
import com.project.iob.post.service.repository.mybatis.PostDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    private final NotificationService notificationService;
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
        
        
     // ✅ 댓글 알림 생성
        try {
            String postAuthorEmail = postDAO.findAuthorEmailByPostId(commentRequestDto.postId());

            if (postAuthorEmail != null && !postAuthorEmail.equals(commentRequestDto.userEmail())) {
                NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
                notiRequest.setUserEmail(postAuthorEmail);       // 알림 받을 사람 (게시글 작성자)
                notiRequest.setNotiType("comment");
                notiRequest.setSenderEmail(commentRequestDto.userEmail()); // 댓글 단 사람
                notiRequest.setMessage("님이 댓글을 달았습니다.");
                notiRequest.setTargetId(commentRequestDto.postId());
                notificationService.createNotification(notiRequest);
            }
        } catch (Exception e) {
            log.warn("댓글 알림 생성 실패: {}", e.getMessage());
        }
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