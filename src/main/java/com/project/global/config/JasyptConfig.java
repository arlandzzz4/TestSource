package com.project.global.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        
         String password = System.getProperty("jasypt.encryptor.password");
         
         if (password == null || password.isEmpty()) {
        	 // 환경변수에서 'JASYPT_PASSWORD'를 읽어옴
             password = System.getenv("JASYPT_PASSWORD");
         }
        
        if (password == null || password.isEmpty()) {
            // 에러를 명확히 찍어줘야 팀원들이 삽질 안 합니다.
            throw new RuntimeException("Jasypt 마스터키를 찾을 수 없습니다!");
        }

        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES"); // 암호화 시 사용한 알고리즘과 일치해야 함
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}