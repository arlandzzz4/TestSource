//챌린지 인증버튼 눌렀을 때 응답
package com.project.iob.challenge.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeLogResponseDto {
    private Long logId;
    private Long challengeId;
    private LocalDate checkedDate;
    private int totalAchieveCount;
}