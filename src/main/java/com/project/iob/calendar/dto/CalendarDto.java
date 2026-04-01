package com.project.iob.calendar.dto;

import lombok.*;
import java.math.BigDecimal;

public class CalendarDto {

    /** 월별 캘린더 하루 응답 */
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DayResponse {
        private BigDecimal weight;
        private Boolean exerciseYn;
        private Integer calorieIntake;
        private Boolean dietLoggedYn;
    }

    /** 체중 저장 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class WeightRequest {
        private String userEmail;     // 요청 유저 이메일
        private String dateKey;       // "2026-3-1"
        private BigDecimal weight;
    }

    /** 운동 체크 저장 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExerciseRequest {
        private String userEmail;     // 요청 유저 이메일
        private String dateKey;
        private Boolean checked;
    }
}
