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

    @Override
    public void insertComment(CommentRequestDto commentRequestDto) {
        commentDAO.insertComment(commentRequestDto);
        try {
            // 게시글 제목 조회
            String postTitle = postDAO.findTitleByPostId(commentRequestDto.postId());

            if (commentRequestDto.parent_comment_id() != null) {
                // 대댓글인 경우
                String commentAuthorEmail = commentDAO.findAuthorEmailByCommentId(commentRequestDto.parent_comment_id());
                if (commentAuthorEmail != null && !commentAuthorEmail.equals(commentRequestDto.userEmail())) {
                    String commentYn = notificationService.getCommentYn(commentAuthorEmail);
                    if ("Y".equals(commentYn)) {
                        // DB 알림 저장
                        NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
                        notiRequest.setUserEmail(commentAuthorEmail);
                        notiRequest.setNotiType("comment");
                        notiRequest.setSenderEmail(commentRequestDto.userEmail());
                        notiRequest.setMessage("님이 '" + postTitle + "'에 대댓글을 달았습니다.");
                        notiRequest.setTargetId(commentRequestDto.postId());
                        notificationService.createNotification(notiRequest);

                        // FCM 푸시 알림 전송
                        final String targetEmail = commentAuthorEmail;
                        userRepository.findById(targetEmail).ifPresent(user -> {
                            if (user.getFcmToken() != null) {
                                Notification fcmNotification = Notification.builder()
                                    .setTitle(postTitle)
                                    .setBody(commentRequestDto.content())
                                    .build();
                                fcmAdminService.sendMessage(user.getFcmToken(), "/notifications", fcmNotification);
                            }
                        });
                    }
                }
            } else {
                // 일반 댓글인 경우
                String postAuthorEmail = postDAO.findAuthorEmailByPostId(commentRequestDto.postId());
                if (postAuthorEmail != null && !postAuthorEmail.equals(commentRequestDto.userEmail())) {
                    String commentYn = notificationService.getCommentYn(postAuthorEmail);
                    if ("Y".equals(commentYn)) {
                        // DB 알림 저장
                        NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
                        notiRequest.setUserEmail(postAuthorEmail);
                        notiRequest.setNotiType("comment");
                        notiRequest.setSenderEmail(commentRequestDto.userEmail());
                        notiRequest.setMessage("님이 '" + postTitle + "'에 댓글을 달았습니다.");
                        notiRequest.setTargetId(commentRequestDto.postId());
                        notificationService.createNotification(notiRequest);

                        // FCM 푸시 알림 전송
                        final String targetEmail = postAuthorEmail;
                        userRepository.findById(targetEmail).ifPresent(user -> {
                            if (user.getFcmToken() != null) {
                                Notification fcmNotification = Notification.builder()
                                    .setTitle(postTitle)
                                    .setBody(commentRequestDto.content())
                                    .build();
                                fcmAdminService.sendMessage(user.getFcmToken(), "/notifications", fcmNotification);
                            }
                        });
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
                    if ("Y".equals(likeYn)) {
                        // DB 알림 저장
                        NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
                        notiRequest.setUserEmail(commentAuthorEmail);
                        notiRequest.setNotiType("like");
                        notiRequest.setSenderEmail(userEmail);
                        notiRequest.setMessage("님이 댓글에 좋아요를 눌렀습니다.");
                        notiRequest.setTargetId(commentId);
                        notificationService.createNotification(notiRequest);

                        // FCM 푸시 알림 전송
                        final String targetEmail = commentAuthorEmail;
                        userRepository.findById(targetEmail).ifPresent(user -> {
                            if (user.getFcmToken() != null) {
                                Notification fcmNotification = Notification.builder()
                                    .setTitle("좋아요 알림")
                                    .setBody(userEmail.split("@")[0] + "님이 댓글에 좋아요를 눌렀습니다.")
                                    .build();
                                fcmAdminService.sendMessage(user.getFcmToken(), "/notifications", fcmNotification);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.warn("댓글 좋아요 알림 생성 실패: {}", e.getMessage());
            }
            return true;
        }
    }
}