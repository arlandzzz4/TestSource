package com.project.iob.calendar.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.iob.calendar.dto.CalendarDietDto;
import com.project.iob.calendar.dto.CalendarDto;
import com.project.iob.calendar.service.CalendarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /** [월별 캘린더 조회] POST /api/calendar/diet */
    // POST → GET 으로 변경
    @GetMapping("/diet")
    public ResponseEntity<Map<String, CalendarDto.DayResponse>> getMonthlyCalendar(
        @RequestParam("year") int year,
        @RequestParam("month") int month,
        @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(
            calendarService.getMonthlyCalendar(email, year, month)
        );
    }

    /** [식단 상세 조회] GET /api/calendar/diet/detail */
    @GetMapping("/diet/detail")
    public ResponseEntity<CalendarDietDto.DietDetailResponse> getDietDetail(
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(
            calendarService.getDietDetail(email, date)
        );
    }

    /** [식단 저장] POST /api/calendar/diet/save */
    @PostMapping("/diet/save")
    public ResponseEntity<Void> saveDiet(
        @RequestBody CalendarDietDto.SaveRequest req
    ) {
        calendarService.saveDiet(req.getUserEmail(), req);
        return ResponseEntity.ok().build();
    }

    /** [체중 저장] PATCH /api/calendar/diet/weight */
    @PatchMapping("/diet/weight")
    public ResponseEntity<Void> saveWeight(
        @RequestBody CalendarDto.WeightRequest req
    ) {
        calendarService.saveWeight(req.getUserEmail(), req);
        return ResponseEntity.ok().build();
    }

    /** [운동 체크 저장] PATCH /api/calendar/diet/exercise */
    @PatchMapping("/diet/exercise")
    public ResponseEntity<Void> saveExercise(
        @RequestBody CalendarDto.ExerciseRequest req
    ) {
        calendarService.saveExercise(req.getUserEmail(), req);
        return ResponseEntity.ok().build();
    }

    /** [음식 검색] GET /api/calendar/food/search?q=닭가슴살&page=0 */
    @GetMapping("/food/search")
    public ResponseEntity<List<CalendarDietDto.FoodSearchResponse>> searchFood(
        @RequestParam("q") String q,
        @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(calendarService.searchFood(q, page));
    }
    
    /** [즐겨먹는 식단 조회] GET /api/calendar/fav-meals */
    @GetMapping("/fav-meals")
    public ResponseEntity<List<CalendarDietDto.FavMealResponse>> getFavMeals(
        @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(calendarService.getFavMeals(email));
    }

    /** [즐겨먹는 식단 저장] POST /api/calendar/fav-meals */
    @PostMapping("/fav-meals")
    public ResponseEntity<Void> saveFavMeal(
        @RequestBody CalendarDietDto.FavMealRequest req
    ) {
        calendarService.saveFavMeal(req.getUserEmail(), req.getName(), req.getItems());
        return ResponseEntity.ok().build();
    }

    /** [즐겨먹는 식단 삭제] DELETE /api/calendar/fav-meals/{favId} */
    @DeleteMapping("/fav-meals/{favId}")
    public ResponseEntity<Void> deleteFavMeal(
        @PathVariable("favId") Long favId,
        @RequestParam("email") String email
    ) {
        calendarService.deleteFavMeal(email, favId);
        return ResponseEntity.ok().build();
    }
}
