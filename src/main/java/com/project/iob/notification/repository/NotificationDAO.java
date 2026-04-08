package com.project.iob.notification.repository;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.dto.NotificationResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationDAO {
    List<NotificationResponseDTO> findByUserEmail(@Param("userEmail") String userEmail);
    void markAsRead(Long notiId);
    void markAllRead(@Param("userEmail") String userEmail);
    void deleteAll(@Param("userEmail") String userEmail);
    void createNotification(NotificationDTO.CreateRequest request);
}