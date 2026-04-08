package com.project.iob.calendar.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.iob.calendar.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByApiFoodCd(String apiFoodCd);
    
    @Query("SELECT f.apiFoodCd FROM Food f")
    List<String> findAllApiFoodCds();
}