package com.project.global.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
//@Profile("prod")
public class FirebaseConfig {
	@Value("${FIREBASE_CONFIG_PATH:fcm-key.json}")///app/config/
	private String configPath;
	
	@PostConstruct
	public void init() {
	    try {
	        if (FirebaseApp.getApps().isEmpty()) {
	            // [변경]: ClassPathResource 대신 FileSystemResource 사용
	            // 이렇게 해야 도커 볼륨으로 넣은 '/app/config/fcm-key.json'을 읽을 수 있습니다.
	            Resource resource = new FileSystemResource(configPath); 
	            InputStream serviceAccount = resource.getInputStream();
	            
	            FirebaseOptions options = FirebaseOptions.builder()
	                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                    .build();
	            FirebaseApp.initializeApp(options);
	            log.info("Firebase 초기화 성공: {}", configPath);
	        }
	    } catch (IOException e) {
	        log.error("Firebase 키 파일을 찾을 수 없습니다: {}", configPath);
	    }
	}
}