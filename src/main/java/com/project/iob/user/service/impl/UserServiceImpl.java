package com.project.iob.user.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.global.auth.Provider;
import com.project.global.error.NeedRegistrationException;
import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.entity.User;
import com.project.iob.user.querydsl.UserRepository;
import com.project.iob.user.repository.mybatis.UserDAO;
import com.project.iob.user.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO; // MyBatis 매퍼 주입
    private final UserRepository userRepository;
    private final EntityManager em;
	
	/**
     * [회원가입 로직]
     */
	@Transactional
	public User regist(UserRequestDto UserRequest) {
		// 중복 체크
		if(Provider.LOCAL.equals(UserRequest.providerCode())) {
	        if (!userRepository.existsByEmail(UserRequest.email())) {
	        	throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
	        }
		} else {
			if (!userRepository.existsByProviderId(UserRequest.providerId())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 소셜 계정입니다.");
	        }
		}
		//패스워드 암호화
		String encodedPassword = passwordEncoder.encode(UserRequest.password());
		
		//엔티티 생성
		User user = User.builder()
				.email(UserRequest.email())
				.password(encodedPassword)
				.providerCode(UserRequest.providerCode())
				.providerId(UserRequest.providerId())
				.build();
		//등록
		user = userRepository.save(user);
		//리턴
		return user;
	}
	
	/**
     * [회원 정보 조회 로직]
     */
	@Transactional(readOnly = true)
	public User searchUserByEmail(String email) {
		User user = userDAO.findByEmail(email)
	            .orElseThrow(() -> new NeedRegistrationException("회원이 아닙니다."));
		return user;
	}

	/**
     * [FCM 토큰 업데이트 로직]
     */
	@Override
	public void updateFcmToken(String email, @NotBlank(message = "fcmToken은 필수 입력 값입니다.") String fcmToken) {
		// TODO 테이블 확정되면 수정
		userDAO.updateFcmToken(email, fcmToken);
	}

	/**
     * [FCM 토큰 클리어 로직]
     */
	@Override
	public void clearFcmToken(String email) {
		// TODO Auto-generated method stub
		userDAO.clearFcmToken(email);
	}
}