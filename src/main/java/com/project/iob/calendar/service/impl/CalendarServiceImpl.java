package com.project.iob.calendar.service.impl;

import com.project.iob.calendar.dto.CalendarDto;
import com.project.iob.calendar.dto.CalendarDietDto;
import com.project.iob.calendar.entity.CalendarDietRecord;
import com.project.iob.calendar.entity.CalendarRecord;
import com.project.iob.calendar.repository.jpa.CalendarDietRecordRepository;
import com.project.iob.calendar.repository.jpa.CalendarRecordRepository;
import com.project.iob.calendar.repository.mybatis.CalendarDAO;
import com.project.iob.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import com.project.iob.calendar.entity.FavMeal;
import com.project.iob.calendar.repository.jpa.FavMealRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRecordRepository calendarRecordRepository;
    private final CalendarDietRecordRepository calendarDietRecordRepository;
    private final CalendarDAO calendarDAO;

    // ※ codes 테이블 실제 값으로 수정 필요
    private static final Map<String, String> MEAL_CODE_MAP = Map.of(
    	    "breakfast", "01",
    	    "lunch",     "02",
    	    "dinner",    "03",
    	    "snack",     "04"
    	);
    	private static final Map<String, String> CODE_MEAL_MAP = Map.of(
    	    "01", "breakfast",
    	    "02", "lunch",
    	    "03", "dinner",
    	    "04", "snack"
    	);

    /** [월별 캘린더 조회] */
    @Override
    @Transactional(readOnly = true)
    public Map<String, CalendarDto.DayResponse> getMonthlyCalendar(String email, int year, int month) {
        List<CalendarRecord> records = calendarRecordRepository.findByUserEmailAndYearMonth(email, year, month);

        Map<String, CalendarDto.DayResponse> result = new LinkedHashMap<>();
        for (CalendarRecord r : records) {
            String key = r.getRecordDate().getYear() + "-"
                       + r.getRecordDate().getMonthValue() + "-"
                       + r.getRecordDate().getDayOfMonth();

            result.put(key, CalendarDto.DayResponse.builder()
                .weight(r.getWeight())
                .exerciseYn(r.getExerciseYn())
                .calorieIntake(r.getCalorieIntake())
                .dietLoggedYn(r.getDietLoggedYn())
                .build());
        }
        return result;
    }

    /** [식단 상세 조회] */
    @Override
    @Transactional(readOnly = true)
    public CalendarDietDto.DietDetailResponse getDietDetail(String email, LocalDate date) {
        List<CalendarDietRecord> records = calendarDietRecordRepository.findByUserEmailAndRecordDate(email, date);

        Map<String, List<CalendarDietDto.FoodItem>> meals = new LinkedHashMap<>();
        meals.put("breakfast", new ArrayList<>());
        meals.put("lunch",     new ArrayList<>());
        meals.put("dinner",    new ArrayList<>());
        meals.put("snack",     new ArrayList<>());

        for (CalendarDietRecord r : records) {
            String mealKey = CODE_MEAL_MAP.getOrDefault(r.getMealTypeCode(), "snack");
            meals.get(mealKey).add(CalendarDietDto.FoodItem.builder()
                .foodId(r.getFoodId())
                .name(r.getFoodName())
                .kcal(r.getCalories())
                .amountG(r.getAmountG())
                .build());
        }
        return CalendarDietDto.DietDetailResponse.builder().meals(meals).build();
    }

    /** [식단 저장] */
    @Override
    public void saveDiet(String email, CalendarDietDto.SaveRequest req) {
        LocalDate date = parseDateKey(req.getDateKey());

        calendarDietRecordRepository.deleteByUserEmailAndRecordDate(email, date);

        int totalCalories = 0;
        for (Map.Entry<String, List<CalendarDietDto.FoodItem>> entry : req.getMeals().entrySet()) {
            String mealCode = MEAL_CODE_MAP.getOrDefault(entry.getKey(), "MEAL004");
            for (CalendarDietDto.FoodItem item : entry.getValue()) {
                calendarDietRecordRepository.save(CalendarDietRecord.builder()
                    .userEmail(email)
                    .recordDate(date)
                    .mealTypeCode(mealCode)
                    .foodId(item.getFoodId())
                    .foodName(item.getName())
                    .calories(item.getKcal())
                    .amountG(item.getAmountG())
                    .build());
                totalCalories += (item.getKcal() != null ? item.getKcal() : 0);
            }
        }

        CalendarRecord cal = calendarRecordRepository
            .findByUserEmailAndRecordDate(email, date)
            .orElse(CalendarRecord.builder().userEmail(email).recordDate(date).build());

        cal.setCalorieIntake(totalCalories);
        cal.setDietLoggedYn(totalCalories > 0);
        if (req.getWeight() != null) {
            cal.setWeight(BigDecimal.valueOf(req.getWeight()));
        }
        calendarRecordRepository.save(cal);
    }

    /** [체중 저장] */
    @Override
    public void saveWeight(String email, CalendarDto.WeightRequest req) {
        LocalDate date = parseDateKey(req.getDateKey());
        CalendarRecord cal = calendarRecordRepository
            .findByUserEmailAndRecordDate(email, date)
            .orElse(CalendarRecord.builder().userEmail(email).recordDate(date).build());
        cal.setWeight(req.getWeight());
        calendarRecordRepository.save(cal);
    }

    /** [운동 체크 저장] */
    @Override
    public void saveExercise(String email, CalendarDto.ExerciseRequest req) {
        LocalDate date = parseDateKey(req.getDateKey());
        CalendarRecord cal = calendarRecordRepository
            .findByUserEmailAndRecordDate(email, date)
            .orElse(CalendarRecord.builder().userEmail(email).recordDate(date).build());
        cal.setExerciseYn(req.getChecked());
        calendarRecordRepository.save(cal);
    }

    /** [음식 검색 — MyBatis] */
    @Override
    @Transactional(readOnly = true)
    public List<CalendarDietDto.FoodSearchResponse> searchFood(String keyword, int page) {
        int size = 30;
        int offset = page * size;
        return calendarDAO.searchFood(keyword, offset, size);
    }

    // "2026-3-1" → LocalDate
    private LocalDate parseDateKey(String dateKey) {
        String[] parts = dateKey.split("-");
        return LocalDate.of(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2])
        );
    }
    
    @Autowired
    private FavMealRepository favMealRepository;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /** [즐겨먹는 식단 조회] */
    @Override
    @Transactional(readOnly = true)
    public List<CalendarDietDto.FavMealResponse> getFavMeals(String email) {
        return favMealRepository.findByUserEmail(email).stream()
            .map(f -> {
                try {
                    List<CalendarDietDto.FoodItem> items = objectMapper.readValue(
                        f.getItems(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CalendarDietDto.FoodItem.class)
                    );
                    return CalendarDietDto.FavMealResponse.builder()
                        .id(f.getFavId())
                        .name(f.getName())
                        .items(items)
                        .build();
                } catch (Exception e) {
                	e.printStackTrace();  // ← 추가
                    log.error("즐겨찾기 저장 실패: {}", e.getMessage(), e);
                    throw new RuntimeException("즐겨찾기 저장 실패", e);
                }
            })
            .filter(f -> f != null)
            .collect(java.util.stream.Collectors.toList());
    }

    /** [즐겨먹는 식단 저장] */
    @Override
    public void saveFavMeal(String email, String name, List<CalendarDietDto.FoodItem> items) {
        try {
            String itemsJson = objectMapper.writeValueAsString(items);
            favMealRepository.save(FavMeal.builder()
                .userEmail(email)
                .name(name)
                .items(itemsJson)
                .build());
        } catch (Exception e) {
            throw new RuntimeException("즐겨찾기 저장 실패", e);
        }
    }

    /** [즐겨먹는 식단 삭제] */
    @Override
    public void deleteFavMeal(String email, Long favId) {
    	
        favMealRepository.deleteByFavIdAndUserEmail(favId, email);
    }
}
