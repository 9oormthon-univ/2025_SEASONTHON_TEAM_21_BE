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
	@Column(name = "REGION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long regionId; // 지역 PK (기본 키)

	@Column(name = "LAWD_CD", nullable = false)   // ★ 여기가 핵심
    private String lawdCd;

    @Column(name = "REGION_KCA_CODE")
    private String regionKcaCode;

    @Column(name = "REGION_SGG_CODE")
    private String regionSggCode;

    @Column(name = "REGION_SGG_NAME")
    private String regionSggName;

    @Column(name = "REGION_SI_NAME")
    private String regionSiName;

    @Column(name = "REGION_STCIS_CODE")
    private String regionStcisCode;

}