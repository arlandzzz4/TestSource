package com.project.global.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String formatMySqlDate(String isoDate) {
        // T 이후를 자르거나 ISO 형식을 파싱
        LocalDateTime dateTime = LocalDateTime.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME);
        
        // MySQL 표준 포맷으로 변환
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}