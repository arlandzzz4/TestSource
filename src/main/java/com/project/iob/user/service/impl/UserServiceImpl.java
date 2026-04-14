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
import com.project.iob.notification.repository.NotificationDAO;
import com.project.iob.user.dto.PasswordChangeRequestDto;
import com.project.iob.user.dto.UnsubscribeRequestDto;
import com.project.iob.user.dto.UserAuthRequestDto;
import com.project.iob.user.dto.UserRequestDto;
import com.project.iob.user.dto.UserResponseDto;
import com.project.iob.user.entity.User;
import com.project.iob.user.repository.mybatis.UserDAO;
import com.project.iob.user.repository.querydsl.UserRepository;
import com.project.iob.user.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO; // MyBatis 매퍼 주입
    private final UserRepository userRepository;
    private final EntityManager em;
    private final NotificationDAO notificationDAO;
	
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
		userRepository.flush(); // 영속성 컨텍스트의 변경 내용을 DB에 반영
		//리턴
		
		// 알림 설정 기본값 INSERT
		notificationDAO.insertDefaultSettings(user.getEmail());

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
	@Transactional
	public void updateFcmToken(String email, @NotBlank(message = "fcmToken은 필수 입력 값입니다.") String fcmToken) {
		userDAO.updateFcmToken(email, fcmToken);
	}

	/**
     * [FCM 토큰 클리어 로직]
     * 
     * 해당 로직을 탈퇴 로직안에 넣어서 api 하나로 관리하도록 변경함
     * 
     * 
     */
	@Override
	@Transactional
	public void clearFcmToken(String email) {
		userDAO.clearFcmToken(email);
	}
	

	/**
     * [날짜별 사용자 조회. 날짜 없을 경우 총 사용자 조회]
     */
	@Override
	public int searchUserCount(UserRequestDto userRequestDto) {
		return userDAO.findUserCount(userRequestDto);
	}

	@Override
	public List<UserResponseDto> searchUsers(UserRequestDto userRequestDto) {
		return userDAO.findUserList(userRequestDto);
	}

	@Override
	@Transactional
	public void updateUserStatusCode(UserRequestDto userRequestDto) {
		userDAO.updateUserStatusCode(userRequestDto);
	}

	//탈퇴할 때 FCM 토큰 비우기와 DB의 유저 상태 변경을 한번에 하도록 변경
	@Override
	@Transactional
	public void unsubscribe(UnsubscribeRequestDto unsubscribeRequestDto) {
	    // 1. FCM 토큰 비우기 먼저 실행
	    userDAO.clearFcmToken(unsubscribeRequestDto.email()); 
	    
	    // 2. 유저 상태 변경 (03으로 변경 및 사유 업데이트)
	    userDAO.unsubscribe(unsubscribeRequestDto);
	    
	 // 3. 파이어베이스(Firebase Auth): 계정 삭제 요청 추가
	    try {
	        // DB에 저장된 이메일을 통해 파이어베이스 유저의 고유 ID(UID)를 조회
	        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(unsubscribeRequestDto.email());
	        
	        // 조회된 UID를 사용하여 파이어베이스 인증 서버에서 계정 삭제
	        FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
	        
	        log.info("파이어베이스 계정 삭제 완료: {}", unsubscribeRequestDto.email());
	    } catch (Exception e) {
	        // 이미 계정이 삭제되었거나 파이어베이스 서버 통신 오류 발생 시 
	        // 전체 탈퇴 로직이 실패(Rollback)하지 않도록 로그만 남기고 예외 처리
	        log.error("파이어베이스 계정 삭제 중 오류 발생: {}", e.getMessage());
	    }
	}
	
	//유저 닉네임 변경
	@Override
	@Transactional
	public void updateNickname(UserRequestDto userRequestDto) {
	    userDAO.updateNickname(userRequestDto);
	}
	
	//비번 변경
	@Override
	@Transactional
	public void updatePassword(PasswordChangeRequestDto dto) {
	    User user = userDAO.findByEmail(dto.email())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

	    if (!Provider.LOCAL.getKey().equals(user.getProviderCode())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "소셜 로그인 회원은 비밀번호를 변경할 수 없습니다.");
	    }

	    userDAO.updatePassword(dto.email(), passwordEncoder.encode(dto.newPassword()));
	}

	@Override
	public int searchNickname(String nickname) {
		return userDAO.findByNickname(nickname);
	}
}