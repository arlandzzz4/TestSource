package com.project.global.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
	LOCAL("01", "로컬"),
	GOOGLE("02", "구글");

    private final String key;
    private final String title;
}
