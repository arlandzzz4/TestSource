package com.project.domain.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.domain.user.dto.UserRequestDto;
import com.project.domain.user.entity.User;
import com.project.domain.user.querydsl.UserRepository;
import com.project.domain.user.repository.mybatis.UserDAO;
import com.project.global.error.NeedRegistrationException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
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
		if("LOCAL".equalsIgnoreCase(UserRequest.provider())) {
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
				.provider(UserRequest.provider())
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
}