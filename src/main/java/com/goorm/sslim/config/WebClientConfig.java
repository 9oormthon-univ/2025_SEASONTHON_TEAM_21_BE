package com.goorm.sslim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Bean
    public WebClient rtmsWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl("http://apis.data.go.kr/1613000") // 공통 루트
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .build();
    }
	
}
