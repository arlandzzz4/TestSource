//챌린지 추가
package com.project.iob.challenge.dto;

import java.time.LocalDate;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRequestDto {

	 private String userEmail;
	 private String title;
	 private String description;
	 private LocalDate startDate;
	 private LocalDate endDate;
	 private int goalDays;
}
