package com.project.iob.notification.repository;
import com.project.iob.admin.usermgmt.dto.FcmUserDto;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.dto.NotificationResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationDAO {
    List<NotificationResponseDTO> findByUserEmail(@Param("userEmail") String userEmail);
    void markAsRead(Long notiId);
    void markAllRead(@Param("userEmail") String userEmail);
    void deleteAll(@Param("userEmail") String userEmail);
    void createNotification(NotificationDTO.CreateRequest request);
    String findCommentYn(@Param("userEmail") String userEmail);
    String findLikeYn(@Param("userEmail") String userEmail);
    Map<String, String> findAllSettingByUserEmail(@Param("userEmail") String userEmail);
    void updateSetting(@Param("userEmail") String userEmail,
                       @Param("likeYn") String likeYn,
                       @Param("commentYn") String commentYn,
                       @Param("noticeYn") String noticeYn);
	List<FcmUserDto> findAllByNotificationEnabledTrue(@Param("offset")long offset, @Param("pageSize")int pageSize);
	long countAllByNotificationEnabledTrue();
	void insertDefaultSettings(@Param("userEmail") String userEmail);
}