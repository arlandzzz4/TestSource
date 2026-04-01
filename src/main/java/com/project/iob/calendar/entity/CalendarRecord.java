package com.project.iob.calendar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CalendarRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "calorie_intake")
    private Integer calorieIntake;

    @Column(name = "exercise_yn")
    private Boolean exerciseYn;

    @Column(name = "diet_logged_yn")
    private Boolean dietLoggedYn;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.exerciseYn == null) this.exerciseYn = false;
        if (this.dietLoggedYn == null) this.dietLoggedYn = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
