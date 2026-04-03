package com.project.iob.admin.usermgmt.controller;

import com.project.iob.calendar.service.FoodApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class FoodApiController {

    private final FoodApiService foodApiService;

    /** 식품 데이터 수동 적재 */
    @PostMapping("/food/fetch")
    public ResponseEntity<String> fetchFoods() {
        foodApiService.fetchAndSaveAll();
        return ResponseEntity.ok("식품 데이터 적재 완료!");
    }
}