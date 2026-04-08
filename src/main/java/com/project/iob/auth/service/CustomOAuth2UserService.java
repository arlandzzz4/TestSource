package com.project.iob.auth.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.iob.user.entity.User;
import com.project.iob.user.repository.querydsl.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	
	private final UserRepository userRepository;

	@Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 구글에서 가져온 기본 유저 정보
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 구글, 네이버, 카카오 중 어디서 왔는지 구분 (registrationId)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. 구글의 유저 정보 필드 추출 (email, name, picture 등)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 4. DB 저장 또는 업데이트 로직
        User user = userRepository.findByEmail(email)
        		.map(entity -> {
                    //entity.update(name); // 기존 유저 정보 업데이트
                    return entity;       // 업데이트된 엔티티 반환
                })
                .orElse(User.builder() // 없으면 신규 가입
                        .email(email)
                        //.name(name)
                        //.role(Role.USER)
                        .build());

        userRepository.save(user);

        // 5. 시큐리티 세션에 저장될 유저 객체 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleCode())),
                attributes,
                "email" // 검증 기준이 될 필드명
        );
    }
}
