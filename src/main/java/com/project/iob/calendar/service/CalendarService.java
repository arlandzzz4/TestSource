package com.project.iob.calendar.service;

import com.project.iob.calendar.dto.CalendarDto;
import com.project.iob.calendar.dto.CalendarDietDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CalendarService {

    /** [월별 캘린더 조회] */
    Map<String, CalendarDto.DayResponse> getMonthlyCalendar(String email, int year, int month);

    /** [식단 상세 조회] */
    CalendarDietDto.DietDetailResponse getDietDetail(String email, LocalDate date);

    /** [식단 저장] */
    void saveDiet(String email, CalendarDietDto.SaveRequest req);

    /** [체중 저장] */
    void saveWeight(String email, CalendarDto.WeightRequest req);

    /** [운동 체크 저장] */
    void saveExercise(String email, CalendarDto.ExerciseRequest req);

    /** [음식 검색] */
    List<CalendarDietDto.FoodSearchResponse> searchFood(String keyword);
    
    /** [즐겨먹는 식단 조회] */
    List<CalendarDietDto.FavMealResponse> getFavMeals(String email);

    /** [즐겨먹는 식단 저장] */
    void saveFavMeal(String email, String name, List<CalendarDietDto.FoodItem> items);

    /** [즐겨먹는 식단 삭제] */
    void deleteFavMeal(String email, Long favId);
}
