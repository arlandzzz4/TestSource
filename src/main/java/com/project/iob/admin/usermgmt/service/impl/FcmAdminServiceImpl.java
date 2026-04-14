package com.project.iob.admin.usermgmt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.project.iob.admin.usermgmt.dto.FcmUserDto;
import com.project.iob.admin.usermgmt.service.FcmAdminService;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmAdminServiceImpl implements FcmAdminService {

	private final NotificationService notificationService;

	@Value("${app.client-url}")
	private String clientUrl;

	@Override
	public void sendMessage(String token, String url, Notification notification) {
		Message message = Message.builder().setToken(token).setNotification(notification).putData("link_url", url)
				.build();
		try {
			String response = FirebaseMessaging.getInstance().send(message);
			log.info("Successfully sent message: " + response);
		} catch (FirebaseMessagingException e) {
			handleFcmException(e, token);
		}
	}

	@Override
	@Transactional
	@Async("notificationExecutor")
	public void sendNoticeNotifications(Long postId, String title, String content) {
		int pageSize = 100;
		int pageNumber = 0;
		int totalSentInBatch = 0;
		String url = createPostLink(postId, "/post/{id}");
		Notification notification = Notification.builder().setTitle(title).setBody(content).build();
		Page<FcmUserDto> userPage;
		NotificationDTO.CreateRequest notiRequest = null;
		do {
			userPage = notificationService.findAllByNotificationEnabledTrue(PageRequest.of(pageNumber, pageSize));

			for (FcmUserDto user : userPage.getContent()) {
				this.sendMessage(user.getFcmToken(), url, notification);

				notiRequest = new NotificationDTO.CreateRequest();
				notiRequest.setUserEmail(user.getEmail());
				notiRequest.setNotiType("notice");
				notiRequest.setSenderEmail("");
				notiRequest.setMessage(title);
				notiRequest.setTargetId(postId);
				notificationService.createNotification(notiRequest);
				totalSentInBatch++;

				if (totalSentInBatch >= 2000) {
					log.info("2,000건 전송 완료. 1시간 휴식에 들어갑니다.");
					try {
						Thread.sleep(3600000);
					} catch (InterruptedException e) {
						log.error("휴식 중 인터럽트 발생", e);
						Thread.currentThread().interrupt();
						return;
					}
					totalSentInBatch = 0;
				}
			}

			pageNumber++;
		} while (userPage.hasNext());

		log.info("모든 알림 전송 완료!");
	}

	private void handleFcmException(FirebaseMessagingException e, String token) {
		if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
				|| e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
			System.err.println("유효하지 않은 토큰 발견. DB에서 삭제 처리가 필요합니다: " + token);
		}
	}

	public String createPostLink(Long postId, String path) {
		return UriComponentsBuilder.fromUriString(clientUrl).path(path).buildAndExpand(postId).toUriString();
	}
}