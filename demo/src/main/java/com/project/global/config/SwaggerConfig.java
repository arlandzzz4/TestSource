package com.project.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";
        // API 요청 시 헤더에 Authorization: Bearer {토큰}이 자동으로 붙도록 설정합니다.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info()
                        .title("내 프로젝트 API 문서")
                        .description("백엔드 API 명세서")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
    //특정 패키지(예: 내 컨트롤러가 있는 곳)만 보여주도록 제한합니다.
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("project-api")
//                .pathsToMatch("/api/**") // 내가 만든 API 경로 패턴
//                .packagesToScan("com.project.domain") // 내 컨트롤러 패키지 경로
//                .build();
//    }
    
    @PostConstruct
    public void init() {
        // 클래스 로딩 시점에 딱 한 번만 실행되도록 설정
        SpringDocUtils.getConfig()
            .replaceWithClass(org.springframework.data.domain.Pageable.class, Void.class)
            .replaceWithClass(org.springframework.data.domain.Sort.class, Void.class);
    }
    
}