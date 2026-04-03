//챌린지 인증버튼 눌렀을 때 요청
package com.project.iob.challenge.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeLogRequestDto {
    private LocalDate checkedDate;
}