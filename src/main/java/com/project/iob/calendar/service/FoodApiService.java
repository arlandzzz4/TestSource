package com.project.iob.calendar.service;

import com.project.iob.calendar.entity.Food;
import com.project.iob.calendar.repository.jpa.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodApiService {

    private final FoodRepository foodRepository;
    private final RestTemplate restTemplate;

    @Value("${food.api.key}")
    private String apiKey;

    @Value("${food.api.url}")
    private String apiUrl;

    private static final int PAGE_SIZE = 500;

    public void fetchAndSaveAll() {
    	log.info("API URL: {}", apiUrl);
        int pageNo = 1;
        int totalSaved = 0;

        while (true) {
            String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", PAGE_SIZE)
                .queryParam("type", "json")
                .build(false)
                .toUriString();

            Map response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> items = parseItems(response);

            if (items == null || items.isEmpty()) break;

            List<Food> foods = new ArrayList<>();
            for (Map<String, Object> item : items) {
                String apiCd = str(item, "FOOD_CD");
                if (foodRepository.existsByApiFoodCd(apiCd)) continue;

                foods.add(Food.builder()
                	    .apiFoodCd(apiCd)
                	    .foodName(str(item, "FOOD_NM_KR"))
                	    .calories(decimal(item, "AMT_NUM1"))
                	    .carbs(decimal(item, "AMT_NUM6"))
                	    .protein(decimal(item, "AMT_NUM3"))
                	    .fat(decimal(item, "AMT_NUM4"))
                	    .servingSize(decimalFromString(item, "SERVING_SIZE"))  // ← 변경
                	    .build());
            }

            foodRepository.saveAll(foods);
            totalSaved += foods.size();
            log.info("페이지 {} 저장 완료 - 누적 {}건", pageNo, totalSaved);

            if (items.size() < PAGE_SIZE) break;
            pageNo++;
        }

        log.info("식품 데이터 전체 저장 완료 - 총 {}건", totalSaved);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseItems(Map response) {
        try {
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            return (List<Map<String, Object>>) body.get("items");
        } catch (Exception e) {
            log.error("API 응답 파싱 실패", e);
            return null;
        }
    }

    private String str(Map<String, Object> item, String key) {
        Object val = item.get(key);
        return val != null ? val.toString() : null;
    }

    private BigDecimal decimal(Map<String, Object> item, String key) {
        try {
            Object val = item.get(key);
            if (val == null) return null;
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
 // "100g" → 100.0 으로 변환
    private BigDecimal decimalFromString(Map<String, Object> item, String key) {
        try {
            Object val = item.get(key);
            if (val == null) return null;
            String str = val.toString().replaceAll("[^0-9.]", "");
            if (str.isEmpty()) return null;
            return new BigDecimal(str);
        } catch (Exception e) {
            return null;
        }
    }
}