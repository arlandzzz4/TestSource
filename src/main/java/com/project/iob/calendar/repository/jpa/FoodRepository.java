package com.project.iob.calendar.repository.jpa;

import com.project.iob.calendar.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByApiFoodCd(String apiFoodCd);
}