package com.project.iob.calendar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fav_meals")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FavMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_id")
    private Long favId;

    @Column(name = "user_email")
    private String userEmail;

    private String name;

    @Column(columnDefinition = "JSON")
    private String items;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}