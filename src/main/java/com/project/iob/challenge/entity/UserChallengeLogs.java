package com.project.iob.challenge.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_challenge_logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    private Long logId;

    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(name = "checked_date", nullable = false)
    private LocalDate checkedDate;
}