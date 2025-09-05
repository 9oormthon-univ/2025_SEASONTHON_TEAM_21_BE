package com.goorm.sslim.region.entity;

import jakarta.persistence.Column;
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
    @Column(name = "REGION_ID")
    private Long regionId; // PK

    @Column(name = "LAWD_CD", nullable = false, unique = true, length = 5)
    private String lawdCd; // 국토부 실거래가 API 요청용 코드 (5자리)

    @Column(name = "SI_NAME", nullable = false, length = 50)
    private String siName; // 시/도 이름 (서울특별시, 경기도 등)

    @Column(name = "SGG_NAME", nullable = false, length = 50)
    private String sggName; // 시군구 이름

}