package com.project.iob.challenge.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_challenges")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallenges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "goal_days", nullable = false)
    private int goalDays;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}