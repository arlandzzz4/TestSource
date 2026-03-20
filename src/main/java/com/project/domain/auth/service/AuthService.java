package com.project.domain.auth.service;

import com.project.domain.auth.dto.LoginRequestDto;
import com.project.domain.auth.dto.TokenDto;

public interface AuthService {

    /**
     * [토큰 재발급 로직]
     */
    public TokenDto reissue(String oldRefreshToken) ;

    /**
     * [로그인 로직]
	  - 소셜 로그인과 일반 로그인 모두 처리
	   - provider가 "LOCAL"이면 email과 password로 인증
	   - provider가 "GOOGLE", "KAKAO" 등 소셜 로그인인 경우 providerId로 인증 (실제 구현에서는 DB에서 사용자 조회 후 providerId로 인증)
     */
	public TokenDto login(LoginRequestDto loginRequest) ;
	
    /**
     * [토큰 검증 로직]
     */
    public void validateStoredToken(String email, String requestToken) ;
	
	/**
     * [로그아웃 로직]
     */
	public void logout(String email, String provider) ;
	
}