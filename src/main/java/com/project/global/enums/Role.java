package com.project.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	USER("01", "ROLE_USER", "일반 사용자"),
    ADMIN("02", "ROLE_ADMIN", "관리자");

    private final String code;     // DB 저장용 ("01", "02")
    private final String roleKey;  // 시큐리티 권한 식별용 ("ROLE_USER")
    private final String title;    // 화면 표시용 ("일반 사용자")
}
