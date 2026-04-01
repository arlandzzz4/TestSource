package com.project.iob.calendar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diet_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CalendarDietRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long dietId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "meal_type_code")
    private String mealTypeCode;

    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "amount_g")
    private Integer amountG;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
