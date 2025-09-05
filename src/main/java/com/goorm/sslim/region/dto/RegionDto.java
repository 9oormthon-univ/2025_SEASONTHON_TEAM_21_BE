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

	private String emd_nm;   // 읍면동 이름
    private String ctpv_cd;  // 시/도 코드
    private String ctpv_nm;  // 시/도 이름
    private String sgg_cd;   // 시군구 코드 (법정동코드 5자리)
    private String sgg_nm;   // 시군구 이름
    private String emd_cd;   // 읍면동 코드 (10자리)
	
}
