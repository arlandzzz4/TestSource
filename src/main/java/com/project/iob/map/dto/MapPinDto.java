package com.project.iob.map.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MapPinDto {

    private Long pinId;           // 핀 고유번호
    private String userEmail;     // 등록자 이메일
    private String nickname;      // 등록자 닉네임 (users 테이블에서 조회)
    private String placeName;     // 시설명
    private String facilityType;  // 시설종류
    private String comment;       // 한줄평
    private Double latitude;      // 위도
    private Double longitude;     // 경도
    private String delYn;         // 삭제여부
    private LocalDateTime createdAt; // 등록일시
}