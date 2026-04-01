package com.project.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStateCode {
	ACTIVE("01", "정상"),
	PAUSED("02", "정지"),
	LEAVE("03", "탈퇴");

    private final String key;
    private final String title;
}
