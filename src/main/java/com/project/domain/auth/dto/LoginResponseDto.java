package com.project.domain.auth.dto;

import com.project.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder // 이 어노테이션이 있어야 .builder() 사용 가능
@AllArgsConstructor // Builder는 모든 필드를 인자로 받는 생성자가 필요함
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA나 Jackson을 위한 기본 생성자
public class LoginResponseDto {
    
    // API 요청 헤더(Authorization)에 넣을 실제 액세스 토큰
    private String accessToken;

    // 사용자 식별을 위한 이메일
    private User user;

}