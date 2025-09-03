package com.goorm.sslim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	private static final String OFFICETEL_API_URL = "http://apis.data.go.kr/1613000/RTMSDataSvcOffiRent";

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(OFFICETEL_API_URL)
            .build();
    }
	
}
