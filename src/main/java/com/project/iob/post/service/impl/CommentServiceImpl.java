package com.project.iob.post.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.Notification;
import com.project.iob.admin.usermgmt.service.FcmAdminService;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.service.NotificationService;
import com.project.iob.post.dto.CommentRequestDto;
import com.project.iob.post.dto.CommentResponseDto;
import com.project.iob.post.service.CommentService;
import com.project.iob.post.service.repository.mybatis.CommentDAO;
import com.project.iob.post.service.repository.mybatis.PostDAO;
import com.project.iob.user.repository.querydsl.UserRepository;

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
    private final FcmAdminService fcmAdminService;
    private final UserRepository userRepository;

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

    // 알림 생성 + FCM 전송 공통 메서드
    private void sendNotification(String toEmail, String senderEmail,
                                   String notiType, String message,
                                   Long targetId, String fcmTitle, String fcmBody) {
        // DB 알림 저장
        NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
        notiRequest.setUserEmail(toEmail);
        notiRequest.setNotiType(notiType);
        notiRequest.setSenderEmail(senderEmail);
        notiRequest.setMessage(message);
        notiRequest.setTargetId(targetId);
        notificationService.createNotification(notiRequest);

        // FCM 푸시 알림 전송
        String postLink = fcmAdminService.createPostLink(targetId, "/post/{id}");
        userRepository.findById(toEmail).ifPresent(user -> {
            if (user.getFcmToken() != null) {
                Notification fcmNotification = Notification.builder()
                    .setTitle(fcmTitle)
                    .setBody(fcmBody)
                    .build();
                fcmAdminService.sendMessage(user.getFcmToken(), postLink, fcmNotification);
            }
        });
    }

    @Override
    public void insertComment(CommentRequestDto commentRequestDto) {
        commentDAO.insertComment(commentRequestDto);
        try {
            String postTitle = postDAO.findTitleByPostId(commentRequestDto.postId());

            if (commentRequestDto.parent_comment_id() != null) {
                // 대댓글인 경우
                String commentAuthorEmail = commentDAO.findAuthorEmailByCommentId(commentRequestDto.parent_comment_id());
                if (commentAuthorEmail != null && !commentAuthorEmail.equals(commentRequestDto.userEmail())) {
                    String commentYn = notificationService.getCommentYn(commentAuthorEmail);
                    if (commentYn == null || "Y".equals(commentYn)) {
                        sendNotification(commentAuthorEmail, commentRequestDto.userEmail(),
                            "comment", "님이 '" + postTitle + "'에 대댓글을 달았습니다.",
                            commentRequestDto.postId(), postTitle, commentRequestDto.content());
                    }
                }
            } else {
                // 일반 댓글인 경우
                String postAuthorEmail = postDAO.findAuthorEmailByPostId(commentRequestDto.postId());
                if (postAuthorEmail != null && !postAuthorEmail.equals(commentRequestDto.userEmail())) {
                    String commentYn = notificationService.getCommentYn(postAuthorEmail);
                    if (commentYn == null || "Y".equals(commentYn)) {
                        sendNotification(postAuthorEmail, commentRequestDto.userEmail(),
                            "comment", "님이 '" + postTitle + "'에 댓글을 달았습니다.",
                            commentRequestDto.postId(), postTitle, commentRequestDto.content());
                    }
                }
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
            try {
                String commentAuthorEmail = commentDAO.findAuthorEmailByCommentId(commentId);
                if (commentAuthorEmail != null && !commentAuthorEmail.equals(userEmail)) {
                    String likeYn = notificationService.getLikeYn(commentAuthorEmail);
                    if (likeYn == null || "Y".equals(likeYn)) {
                        sendNotification(commentAuthorEmail, userEmail,
                            "like", "님이 댓글에 좋아요를 눌렀습니다.",
                            commentId, "좋아요 알림",
                            userEmail.split("@")[0] + "님이 댓글에 좋아요를 눌렀습니다.");
                    }
                }
            } catch (Exception e) {
                log.warn("댓글 좋아요 알림 생성 실패: {}", e.getMessage());
            }
            return true;
        }
    }
}