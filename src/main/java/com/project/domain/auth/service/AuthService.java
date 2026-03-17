package com.project.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.domain.auth.dto.LoginRequestDto;
import com.project.domain.auth.dto.TokenDto;
import com.project.domain.auth.entity.RefreshToken;
import com.project.domain.auth.repository.querydsl.RefreshTokenRepository;
import com.project.global.error.NeedRegistrationException;
import com.project.global.security.JwtTokenProvider;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;

    /**
     * [토큰 재발급 로직]
     */
    @Transactional
    public TokenDto reissue(String oldRefreshToken) {
        if (!tokenProvider.validateToken(oldRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // username 추출 key이름이 username일 뿐 실제 사람 이름은 아님. tokenProvider에서 인증 객체를 얻어 username을 추출(로그아웃된 사용자도 여기서 걸러짐)
        String username = tokenProvider.getAuthentication(oldRefreshToken).getName();
        
        // DB 조회 (id를 username으로 쓰고 있으므로 findById 가능)
        var savedToken = refreshTokenRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("로그아웃된 사용자입니다."));

        // [토큰 재사용 탐지 및 Race Condition 방어]
        if (oldRefreshToken != null && !oldRefreshToken.equals(savedToken.getRefreshToken())) {
            // 2초 이내 중복 요청 처리 (클라이언트 사이드 중복 클릭 등)
            if (savedToken.getTokenRotatedAt().isAfter(LocalDateTime.now().minusSeconds(2))) {
                return new TokenDto(tokenProvider.createAccessToken(username), savedToken.getRefreshToken());
            }

            // 보안 위협: 이미 사용된 토큰이 다시 들어옴
            refreshTokenRepository.delete(savedToken);
            throw new IllegalStateException("보안 위협이 감지되어 강제 로그아웃되었습니다.");
        }

        // 정상 Rotation
        String newAccessToken = tokenProvider.createAccessToken(username);
        String newRefreshToken = tokenProvider.createRefreshToken(username);
        
        savedToken.updateToken(newRefreshToken);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    /**
     * [로그인 로직]
	  - 소셜 로그인과 일반 로그인 모두 처리
	   - provider가 "LOCAL"이면 email과 password로 인증
	   - provider가 "GOOGLE", "KAKAO" 등 소셜 로그인인 경우 providerId로 인증 (실제 구현에서는 DB에서 사용자 조회 후 providerId로 인증)
     */
    @Transactional
	public TokenDto login(LoginRequestDto loginRequest) {
    	// 1. 식별자 결정 (LOCAL인 경우 이메일, 그 외에는 소셜 ID 등)
    	String identifier = "local".equalsIgnoreCase(loginRequest.provider()) 
    	                    ? loginRequest.email() 
    	                    : loginRequest.providerId();

    	// 2. DB에서 사용자 조회 및 "인증" 먼저 수행
    	RefreshToken rt = refreshTokenRepository.findByEmail(identifier, loginRequest.provider())
    	    .orElseThrow(() -> new NeedRegistrationException("등록된 사용자가 아닙니다."));

    	// 3. 비밀번호 검증 (검증 실패 시 여기서 바로 예외 던짐)
    	if (!passwordEncoder.matches(loginRequest.password(), rt.getPassword())) {
    	    throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
    	}

    	// 4. 인증에 성공했으므로 이제 "토큰 발급"
    	String accessToken = tokenProvider.createAccessToken(identifier);
    	String refreshToken = tokenProvider.createRefreshToken(identifier);

    	// 5. DB의 RefreshToken 값 업데이트 (Dirty Checking)
    	rt.updateToken(refreshToken);

    	return new TokenDto(accessToken, refreshToken);
	}
	
    /**
     * [토큰 검증 로직]
     */
	@Transactional(readOnly = true)
    public void validateStoredToken(String email, String requestToken) {
        // 2. [조회 로직] DB에 저장된 토큰과 요청받은 토큰이 일치하는지 검증
        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));

        if (!savedToken.getRefreshToken().equals(requestToken)) {
            throw new RuntimeException("토큰 정보가 일치하지 않습니다. 다시 로그인해주세요.");
        }
    }
	
	/**
     * [로그아웃 로직]
     */
	@Transactional
	public void logout(String email, String provider) {
        // DB에서 해당 유저의 리프레시 토큰을 삭제하여 재발급을 원천 차단
        refreshTokenRepository.deleteRefreshTokenById(email, provider);
    }
	
}