package com.project.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"local", "dev"}) // S3를 쓰는 prod 환경에서는 이 설정이 무시됩니다.
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.resource-path}")
    private String resourcePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath) // 브라우저에서 접근할 경로
                .addResourceLocations("file:" + uploadPath); // 실제 파일이 있는 물리 경로
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
        		.allowedOrigins("https://information-of-balance.xyz",
        				"https://www.information-of-balance.xyz",
        				"http://localhost:8080", // 스웨거 로컬
        				"http://localhost:3000"// 리액트 로컬
        				) // 실제 허용할 도메인으로 변경
                //.allowedOrigins("*") // 모든 도메인 허용 (테스트용)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
