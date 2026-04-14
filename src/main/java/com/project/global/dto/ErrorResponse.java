package com.project.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;    // 에러 구분 코드 (예: "AUTH_001")
    private String message; // 사용자에게 보여줄 메시지
}
