package com.goorm.sslim.region.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "REGION")
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long regionId; // 지역 PK (기본 키)

    private String regionSiName; // 시/도명 (예: "서울특별시")

    private String regionSggName; // 시군구명 (예: "종로구")

    private String regionSggCode; // 시군구 코드 (행정동 코드와 동일 운용 가능)

    private String lawdCd; // 국토교통부 전월세 실거래 API 코드 (LAWD_CD)

    private String regionKcaCode; // 한국소비자원 참가격 지역코드

    private String regionStcisCode; // 교통카드(STCIS) 지역코드
	
}