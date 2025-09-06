package com.goorm.sslim.income.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDto {

	private String year;       // 과세 연도
    private String group;      // 1분위, 2분위, 3분위, 4분위, 5분위
    private Long boundary;     // 해당 분위의 경계값 (소득)
	
}
