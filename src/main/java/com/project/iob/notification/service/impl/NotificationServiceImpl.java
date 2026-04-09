package com.project.iob.notification.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.project.iob.admin.usermgmt.dto.FcmUserDto;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.dto.NotificationResponseDTO;
import com.project.iob.notification.repository.NotificationDAO;
import com.project.iob.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final NotificationDAO notificationDAO;

	@Override
	public List<NotificationResponseDTO> getNotifications(String userEmail) {
		log.info("받은 이메일: '{}'", userEmail);
		log.info("이메일 길이: {}", userEmail.length());
		List<NotificationResponseDTO> result = notificationDAO.findByUserEmail(userEmail);
		log.info("Map 결과 개수: {}", result.size());
		log.info("Map 데이터: {}", result);
		return result;
	}

	@Override
	public void markAsRead(Long notiId) {
		notificationDAO.markAsRead(notiId);
	}

	@Override
	public void markAllRead(String userEmail) {
		notificationDAO.markAllRead(userEmail);
	}

	@Override
	public void deleteAll(String userEmail) {
		notificationDAO.deleteAll(userEmail);
	}

	@Override
	public void createNotification(NotificationDTO.CreateRequest request) {
		notificationDAO.createNotification(request);
	}

	@Override
	public Map<String, String> getAllSettings(String userEmail) {
		return notificationDAO.findAllSettingByUserEmail(userEmail);
	}

	@Override
	public void updateNotificationSetting(String userEmail, String likeYn, String commentYn, String noticeYn) {
		notificationDAO.updateSetting(userEmail, likeYn, commentYn, noticeYn);
	}
	
	@Override
	public String getCommentYn(String userEmail) {
	    return notificationDAO.findCommentYn(userEmail);
	}

	@Override
	public String getLikeYn(String userEmail) {
	    return notificationDAO.findLikeYn(userEmail);
	}

	@Override
	public Page<FcmUserDto> findAllByNotificationEnabledTrue(PageRequest pageable) {
		List<FcmUserDto> content = notificationDAO.findAllByNotificationEnabledTrue(pageable.getOffset(), pageable.getPageSize());
		    
	    // 2. 전체 개수 가져오기
	    long total = notificationDAO.countAllByNotificationEnabledTrue();
	    
	    return new PageImpl<>(content, pageable, total);
	}
}