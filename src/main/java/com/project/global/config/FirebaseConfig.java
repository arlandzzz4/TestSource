package com.project.global.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("prod")
public class FirebaseConfig {
	@Value("${FIREBASE_CONFIG_PATH:/app/config/fcm-key.json}")
	private String configPath;
	
    @PostConstruct
    public void init() {
        try {
        	if (FirebaseApp.getApps().isEmpty()) {
	            FileInputStream serviceAccount = new FileInputStream(configPath);
	            FirebaseOptions options = FirebaseOptions.builder()
	                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                    .build();
	            FirebaseApp.initializeApp(options);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}