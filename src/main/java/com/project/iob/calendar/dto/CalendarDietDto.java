package com.project.iob.calendar.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

public class CalendarDietDto {

    /** 식단 상세 응답 */
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DietDetailResponse {
        private Map<String, List<FoodItem>> meals;
    }

    /** 음식 항목 */
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FoodItem {
        private Long foodId;
        private String name;
        private Integer kcal;
        private Integer amountG;
    }

    /** 식단 저장 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SaveRequest {
        private String userEmail;                        // 요청 유저 이메일
        private String dateKey;
        private Map<String, List<FoodItem>> meals;
        private Double weight;
    }

    /** 음식 검색 응답 */
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FoodSearchResponse {
        private Long foodId;
        private String name;
        private Integer kcal;
        private String unit;
    }
    
    /** 즐겨먹는 식단 응답 */
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FavMealResponse {
        private Long id;
        private String name;
        private List<FoodItem> items;
    }

    /** 즐겨먹는 식단 저장 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class FavMealRequest {
        private String userEmail;
        private String name;
        private List<FoodItem> items;
    }
}
