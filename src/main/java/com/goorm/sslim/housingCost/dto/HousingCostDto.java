package com.goorm.sslim.housingCost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousingCostDto {

	private Long housingCostId;
	
	private Long regionId;
	
	private String housingType; 	// 주거형태 (예: "Officetel", "Apartment")
	
    private double exclusiveArea; 	// 전용면적 (excluUseAr)
    
    private double deposit; 		// 보증금 (deposit)
    
    private double monthlyRent; 	// 월세 (monthlyRent)
    
    private String sggNm; 			// 시군구 (sggNm)
    
    private String umdNm;			// 법정동 (umdNm)
    
    private int dealYear;			// 계약년도 (dealYear)
    
    private int dealMonth;			// 계약월 (dealMonth)
	
}
