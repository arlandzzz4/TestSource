package com.project.global.error; 

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String code;
    private String message;

    // 정적 팩토리 메서드 (of 사용으로 생성을 깔끔하게 함)
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}