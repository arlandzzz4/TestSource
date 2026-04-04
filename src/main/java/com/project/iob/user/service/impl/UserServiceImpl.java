package com.project.iob.user.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.global.enums.Provider;
import com.project.global.enums.Role;
import com.project.global.enums.UserStateCode;
import com.project.iob.user.dto.UserAuthRequestDto;
import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.dto.UserResponseDto;
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
	public User regist(UserAuthRequestDto UserRequest) {
		// 중복 체크
		String encodedPassword = "";
		if(Provider.LOCAL.getKey().equals(UserRequest.providerCode())) {
	        if (!userRepository.existsByEmail(UserRequest.email())) {
	        	throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
	        }
	        //패스워드 암호화
	        encodedPassword = passwordEncoder.encode(UserRequest.password());
		} else {
			if (!userRepository.existsByProviderId(UserRequest.providerId())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 소셜 계정입니다.");
	        }
		}
		
		//엔티티 생성
		User user = User.builder()
				.email(UserRequest.email())
				.nickname(UserRequest.nickname())
				.password(encodedPassword)
				.providerCode(UserRequest.providerCode())
				.providerId(UserRequest.providerId())
				.roleCode(Role.USER.getCode()) // 기본 권한 설정
				.termsAgreedYn(UserRequest.termsAgreedYn())
				.privacyAgreedYn(UserRequest.privacyAgreedYn())
				.userStatusCode(UserStateCode.ACTIVE.getKey()) // 기본 상태 설정
				.build();
		//등록
		user = userRepository.save(user);
		em.flush(); // 영속성 컨텍스트의 변경 내용을 DB에 반영
		//리턴
		return user;
	}
	
	/**
     * [회원 정보 조회 로직]
     */
	@Transactional(readOnly = true)
	public User searchUserByEmail(String email) {
		User user = userDAO.findByEmail(email)
				.orElseGet(() -> User.createEmpty());
		return user;
	}

	/**
     * [FCM 토큰 업데이트 로직]
     */
	@Override
	public void updateFcmToken(String email, @NotBlank(message = "fcmToken은 필수 입력 값입니다.") String fcmToken) {
		userDAO.updateFcmToken(email, fcmToken);
	}

	/**
     * [FCM 토큰 클리어 로직]
     */
	@Override
	public void clearFcmToken(String email) {
		userDAO.clearFcmToken(email);
	}

	/**
     * [날짜별 사용자 조회. 날짜 없을 경우 총 사용자 조회]
     */
	@Override
	public int searchUserCount(String roleCode, String oneMonthAgo, String today) {
		return userDAO.findUserCount(roleCode, oneMonthAgo, today);
	}

	@Override
	public List<UserResponseDto> searchUsers(UserRequestDto userRequestDto) {
		return userDAO.findUserList(userRequestDto);
	}

	@Override
	public void updateUserStatusCode(UserRequestDto userRequestDto) {
		userDAO.updateUserStatusCode(userRequestDto);
	}
}