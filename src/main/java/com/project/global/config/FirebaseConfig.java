package com.project.global.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("prod")
public class FirebaseConfig {
	@Value("${FIREBASE_CONFIG_PATH:fcm-key.json}")///app/config/
	private String configPath;
	
    @PostConstruct
    public void init() {
        try {
        	if (FirebaseApp.getApps().isEmpty()) {
        		Resource resource = new ClassPathResource(configPath);
                InputStream serviceAccount = resource.getInputStream();
                
	            FirebaseOptions options = FirebaseOptions.builder()
	                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                    .build();
	            FirebaseApp.initializeApp(options);
	            log.info("Firebase Application has been initialized");
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}