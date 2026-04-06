package com.project.iob.calendar.repository.jpa;

import com.project.iob.calendar.entity.FavMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavMealRepository extends JpaRepository<FavMeal, Long> {
    List<FavMeal> findByUserEmail(String userEmail);
    void deleteByFavIdAndUserEmail(Long favId, String userEmail);
}