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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDetailServiceImpl implements PostDetailService {

	private final PostDetailDAO postDetailDAO;
	private final NotificationService notificationService;
	private final FcmAdminService fcmAdminService; // ✅ 추가
	private final UserRepository userRepository; // ✅ 추가

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
			return false;
		} else {
			postDetailDAO.insertPostLike(postId, userEmail);
			try {
				String postAuthorEmail = postDetailDAO.findAuthorEmailByPostId(postId);
				if (postAuthorEmail != null && !postAuthorEmail.equals(userEmail)) {

					// 좋아요 알림 설정 체크
					String likeYn = notificationService.getLikeYn(postAuthorEmail);
					if ("Y".equals(likeYn)) {
						// DB 알림 저장
						NotificationDTO.CreateRequest notiRequest = new NotificationDTO.CreateRequest();
						notiRequest.setUserEmail(postAuthorEmail);
						notiRequest.setNotiType("like");
						notiRequest.setSenderEmail(userEmail);
						notiRequest.setMessage("님이 좋아요를 눌렀습니다.");
						notiRequest.setTargetId(postId);
						notificationService.createNotification(notiRequest);

						// FCM 푸시 알림 전송
						final String targetEmail = postAuthorEmail;
						userRepository.findById(targetEmail).ifPresent(user -> {
							if (user.getFcmToken() != null) {
								// 게시글 제목 조회
								String postTitle = postDetailDAO.getTitleByPostId(postId);
								Notification fcmNotification = Notification.builder().setTitle(postTitle)
										.setBody(userEmail.split("@")[0] + "님이 좋아요를 눌렀습니다.").build();
								fcmAdminService.sendMessage(user.getFcmToken(), "/notifications", fcmNotification);
							}
						});
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