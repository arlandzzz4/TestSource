package com.project.iob.calendar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "foods")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "api_food_cd")
    private String apiFoodCd;

    @Column(name = "food_name")
    private String foodName;

    private BigDecimal calories;
    private BigDecimal carbs;
    private BigDecimal protein;
    private BigDecimal fat;

    @Column(name = "serving_size")
    private BigDecimal servingSize;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}