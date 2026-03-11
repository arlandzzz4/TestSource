package com.project.domain.auth.service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.domain.auth.dto.LoginRequestDto;
import com.project.domain.auth.dto.RegistUserRequestDto;
import com.project.domain.auth.dto.TokenDto;
import com.project.domain.auth.entity.RefreshToken;
import com.project.domain.auth.entity.Users;
import com.project.domain.auth.repository.mybatis.UserAuthDAO;
import com.project.domain.auth.repository.querydsl.RefreshTokenRepository;
import com.project.domain.auth.repository.querydsl.UserRepository;
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
    private final UserAuthDAO userAuthDAO; // MyBatis 매퍼 주입
    private final UserRepository userRepository;
    private final EntityManager em;

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
        if (oldRefreshToken != null && !oldRefreshToken.equals(savedToken.getToken())) {
            // 2초 이내 중복 요청 처리 (클라이언트 사이드 중복 클릭 등)
            if (savedToken.getRotatedAt().isAfter(LocalDateTime.now().minusSeconds(2))) {
                return new TokenDto(tokenProvider.createAccessToken(username), savedToken.getToken());
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

    @Transactional
	public TokenDto login(LoginRequestDto loginRequest) {
		//password 암호화 bcrypt로 암호화된 비밀번호와 비교해야 합니다.
		String encodedPassword = passwordEncoder.encode(loginRequest.password());
		
		//인증 성공 시 토큰 발급
		AtomicReference<String> email = new AtomicReference<String>();
		if("LOCAL".equalsIgnoreCase(loginRequest.provider())) {
			email.set(loginRequest.email());
		} else {
			email.set(loginRequest.providerId()); // 실제로는 DB에서 조회한 사용자 이름을 사용해야 합니다.
		}
		
	    String accessToken = tokenProvider.createAccessToken(email.get());
	    String refreshToken = tokenProvider.createRefreshToken(email.get());

	    //DB에서 사용자 조회 및 인증 로직
	    RefreshToken rt = refreshTokenRepository.findByEmail(email.get(), loginRequest.provider(), encodedPassword)
	        .map(token -> {
	            // 이미 있다면 새로운 토큰값으로 업데이트 (Dirty Checking 발생)
	            token.updateToken(refreshToken);
	            return token;
	        })
	        .orElseThrow(() -> new NeedRegistrationException("등록된 사용자가 아닙니다. 회원가입으로 이동합니다."));
	    refreshTokenRepository.save(rt);
	     
	    return new TokenDto(accessToken, refreshToken);
	}
	
	@Transactional(readOnly = true)
    public void validateStoredToken(String email, String requestToken) {
        // 2. [조회 로직] DB에 저장된 토큰과 요청받은 토큰이 일치하는지 검증
        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));

        if (!savedToken.getToken().equals(requestToken)) {
            throw new RuntimeException("토큰 정보가 일치하지 않습니다. 다시 로그인해주세요.");
        }
    }
	
	@Transactional
	public void logout(String email, String provider) {
        // DB에서 해당 유저의 리프레시 토큰을 삭제하여 재발급을 원천 차단
        refreshTokenRepository.deleteRefreshTokenById(email, provider);
    }
	
	@Transactional
	public Users regist(RegistUserRequestDto registUserRequest) {
		// 중복 체크
		if("LOCAL".equalsIgnoreCase(registUserRequest.provider())) {
	        if (!userRepository.existsByEmail(registUserRequest.email())) {
	        	throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
	        }
		} else {
			if (!userRepository.existsByProviderId(registUserRequest.providerId())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 소셜 계정입니다.");
	        }
		}
		//패스워드 암호화
		String encodedPassword = passwordEncoder.encode(registUserRequest.password());
		
		//엔티티 생성
		Users users = Users.builder()
				.email(registUserRequest.email())
				.password(encodedPassword)
				.provider(registUserRequest.provider())
				.build();
		//등록
		users = userRepository.save(users);
		//리턴
		return users;
	}
	
	@Transactional(readOnly = true)
	public Users test2(String email) {
		Users user = userAuthDAO.findByEmail(email)
	            .orElseThrow(() -> new NeedRegistrationException("회원이 아닙니다."));
		return user;
	}
}