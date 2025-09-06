package com.goorm.sslim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 API 경로 허용
                        .allowedOrigins("http://localhost:3000", "http://localhost:5173") // 프론트엔드 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")       // 허용할 메서드
                        .allowedHeaders("*")                                            // 모든 헤더 허용
                        .allowCredentials(true);                                        // 쿠키 포함 여부
            }
        };
    }
}
