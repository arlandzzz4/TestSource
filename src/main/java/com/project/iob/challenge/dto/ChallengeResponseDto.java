//챌린지 목록 불러오기
package com.project.iob.challenge.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponseDto {
    private Long challengeId;
    private String userEmail;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int goalDays;
    
    private int achieveCount;
    private LocalDate lastCheckedDate;
}
