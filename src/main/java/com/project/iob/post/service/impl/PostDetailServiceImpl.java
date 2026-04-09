package com.project.iob.post.service.impl;

import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.service.NotificationService;
import com.project.iob.post.dto.PostDetailDto;
import com.project.iob.post.service.PostDetailService;
import com.project.iob.post.service.repository.mybatis.PostDetailDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDetailServiceImpl implements PostDetailService {

    private final PostDetailDAO postDetailDAO;
    private final NotificationService notificationService;

    @Override
    public PostDetailDto getPostDetail(Long postId, String userEmail) {
        PostDetailDto post = postDetailDAO.getPostDetail(postId);
        int liked = postDetailDAO.checkPostLike(postId, userEmail);
        post.setLiked(liked > 0);
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
            return false; // 좋아요 취소
        } else {
            postDetailDAO.insertPostLike(postId, userEmail);
            try {
                String postAuthorEmail = postDetailDAO.findAuthorEmailByPostId(postId);
                if (postAuthorEmail != null && !postAuthorEmail.equals(userEmail)) {
                    
                    // 좋아요 알림 설정 체크
                    String likeYn = notificationService.getLikeYn(postAuthorEmail);
                    if ("Y".equals(likeYn)) {
                        NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
                        notiRequest.setUserEmail(postAuthorEmail);
                        notiRequest.setNotiType("like");
                        notiRequest.setSenderEmail(userEmail);
                        notiRequest.setMessage("님이 좋아요를 눌렀습니다.");
                        notiRequest.setTargetId(postId);
                        notificationService.createNotification(notiRequest);
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