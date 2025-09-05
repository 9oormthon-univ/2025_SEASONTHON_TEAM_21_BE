package com.goorm.sslim.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDto {

	private Long regionId; 			// 지역 PK (기본 키)
	
    private String regionSiName; 	// 시/도명
    
    private String regionSggName; 	// 시군구명
    
    private String regionSggCode; 	// 시군구 코드
    
    private String lawdCd; 			// 국토교통부 API 코드
    
    private String regionKcaCode; 	// 한국소비자원 API 코드
    
    private String regionStcisCode; // 교통카드 API 코드
	
}
