package com.project.global.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
	
	public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    
    // 인스턴스화 방지
    private DateUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * LocalDateTime -> String (yyyy-MM-dd HH:mm:ss)
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * String -> LocalDateTime (포맷 불일치 시 예외 발생 주의)
     */
    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 상대 시간 계산 (방금 전, n분 전, n일 전 등)
     */
    public static String toRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "방금 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86400) return (seconds / 3600) + "시간 전";
        if (seconds < 2592000) return (seconds / 86400) + "일 전";
        
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * 특정 날짜의 시작 시간 (00:00:00)
     */
    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    /**
     * 특정 날짜의 종료 시간 (23:59:59.999...)
     */
    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }
	
    /**
     * isoDate (예: 2024-06-01T12:34:56) -> MySQL 표준 포맷 (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatMySqlDate(String isoDate) {
        // T 이후를 자르거나 ISO 형식을 파싱
        LocalDateTime dateTime = LocalDateTime.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME);
        
        // MySQL 표준 포맷으로 변환
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}