package com.goorm.sslim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Bean(name = "rtmsWebClient")
    public WebClient rtmsWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl("http://apis.data.go.kr/1613000") // 국토부 실거래가 공통 루트
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .build();
    }
	
	@Bean(name = "regionalCodeWebClient")
    public WebClient regionalCodeWebClient(WebClient.Builder builder) {
        return builder
        	.baseUrl("https://apis.data.go.kr/1613000/RegionalCode") // 국토부 지역코드 루트
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
	
}
