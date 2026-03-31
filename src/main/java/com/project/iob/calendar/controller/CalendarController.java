package com.project.iob.calendar.controller;

import com.project.iob.calendar.dto.CalendarDto;
import com.project.iob.calendar.dto.CalendarDietDto;
import com.project.iob.calendar.service.CalendarService;
import com.project.iob.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /** [월별 캘린더 조회] POST /api/calendar/diet */
    @PostMapping("/diet")
    public ResponseEntity<Map<String, CalendarDto.DayResponse>> getMonthlyCalendar(
        @RequestParam("year") int year,
        @RequestParam("month") int month,
        @RequestBody User user
    ) {
        return ResponseEntity.ok(
            calendarService.getMonthlyCalendar(user.getEmail(), year, month)
        );
    }

    /** [식단 상세 조회] POST /api/calendar/diet/detail */
    @PostMapping("/diet/detail")
    public ResponseEntity<CalendarDietDto.DietDetailResponse> getDietDetail(
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestBody User user
    ) {
        return ResponseEntity.ok(
            calendarService.getDietDetail(user.getEmail(), date)
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

    /** [음식 검색] GET /api/calendar/food/search?q=닭가슴살 */
    @GetMapping("/food/search")
    public ResponseEntity<List<CalendarDietDto.FoodSearchResponse>> searchFood(
        @RequestParam("q") String q
    ) {
        return ResponseEntity.ok(calendarService.searchFood(q));
    }
}
