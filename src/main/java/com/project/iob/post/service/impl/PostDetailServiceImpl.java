package com.project.iob.post.service.impl;

import com.google.firebase.messaging.Notification;
import com.project.iob.admin.usermgmt.service.FcmAdminService;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.service.NotificationService;
import com.project.iob.post.dto.PostDetailDto;
import com.project.iob.post.service.PostDetailService;
import com.project.iob.post.service.repository.mybatis.PostDetailDAO;
import com.project.iob.user.repository.querydsl.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDetailServiceImpl implements PostDetailService {

    private final PostDetailDAO postDetailDAO;
    private final NotificationService notificationService;
    private final FcmAdminService fcmAdminService;
    private final UserRepository userRepository;

    // ✅ 알림 생성 + FCM 전송 공통 메서드
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
    public PostDetailDto getPostDetail(Long postId, String userEmail) {
        PostDetailDto post = postDetailDAO.getPostDetail(postId);
        int liked = postDetailDAO.checkPostLike(postId, userEmail);
        post.setLiked(liked > 0);
        List<String> imageUrls = postDetailDAO.getImageUrls(postId);
        post.setImageUrls(imageUrls);
        return post;
    }

    @Override
    public void updatePost(Long postId, String title, String content, String categoryCode) {
        postDetailDAO.updatePost(postId, title, content, categoryCode);
    }

    @Override
    public void deletePost(Long postId, String userEmail) {
        postDetailDAO.deletePost(postId, userEmail);
    }

    @Override
    public boolean togglePostLike(Long postId, String userEmail) {
        int count = postDetailDAO.checkPostLike(postId, userEmail);
        if (count > 0) {
            postDetailDAO.deletePostLike(postId, userEmail);
            return false;
        } else {
            postDetailDAO.insertPostLike(postId, userEmail);
            try {
                String postAuthorEmail = postDetailDAO.findAuthorEmailByPostId(postId);
                if (postAuthorEmail != null && !postAuthorEmail.equals(userEmail)) {
                    if ("Y".equals(notificationService.getLikeYn(postAuthorEmail))) {
                        String postTitle = postDetailDAO.getTitleByPostId(postId);
                        sendNotification(postAuthorEmail, userEmail,
                            "like", "님이 좋아요를 눌렀습니다.",
                            postId, postTitle,
                            userEmail.split("@")[0] + "님이 좋아요를 눌렀습니다.");
                    }
                }
            } catch (Exception e) {
                log.warn("좋아요 알림 생성 실패: {}", e.getMessage());
            }
            return true;
        }
    }

    @Override
    public void insertReport(String targetCode, Long targetId, String reporterEmail, String reasonCode) {
        postDetailDAO.insertReport(targetCode, targetId, reporterEmail, reasonCode);
    }
}