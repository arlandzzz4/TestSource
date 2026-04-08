package com.project.iob.notification.controller;

import com.project.iob.notification.dto.NotificationResponseDTO;
import com.project.iob.notification.dto.NotificationDTO;
import com.project.iob.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Notification API", description = "알림 API")
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
    	    @RequestParam(value = "userEmail") String userEmail
    	) {
    	    List<NotificationResponseDTO> result = notificationService.getNotifications(userEmail);
    	    log.info("~~~~~~~~~~~~~~~~~" + result.size());
    	    
    	    return ResponseEntity.ok(result);
    	}

    @Operation(summary = "단일 알림 읽음 처리")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "읽음 처리 성공"),
        @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
    })
    @PatchMapping("/{notiId}/read")
    public ResponseEntity<Void> markAsRead(
        @PathVariable(value = "notiId") Long notiId
    ) {
        notificationService.markAsRead(notiId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 알림 읽음 처리")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "전체 읽음 처리 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(
        @RequestParam(value = "userEmail") String userEmail
    ) {
        notificationService.markAllRead(userEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 알림 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "전체 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(
        @RequestParam(value = "userEmail") String userEmail
    ) {
        notificationService.deleteAll(userEmail);
        return ResponseEntity.noContent().build();
    }
}