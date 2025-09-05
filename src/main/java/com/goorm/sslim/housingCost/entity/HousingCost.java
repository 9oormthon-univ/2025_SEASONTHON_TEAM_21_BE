package com.goorm.sslim.housingCost.entity;

import com.goorm.sslim.housingCost.service.HousingCostService.HousingType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "housing_cost")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousingCost {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HOUSING_COST_ID")
    private Long housingCostId;

	@Column(name = "REGION_ID")
    private Long regionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "HOUSING_TYPE", nullable = false)
    private HousingType housingType;

    @Column(name = "EXCLUSIVE_AREA", nullable = false)
    private double exclusiveArea;

    @Column(name = "DEPOSIT", nullable = false)
    private double deposit;

    @Column(name = "MONTHLY_RENT", nullable = false)
    private double monthlyRent;

    @Column(name = "SGG_NM", nullable = true)
    private String sggNm;

    @Column(name = "SGG_CD", nullable = true)
    private String sggCd;

    @Column(name = "UMD_NM", nullable = false)
    private String umdNm;

    @Column(name = "DEAL_YEAR", nullable = false)
    private int dealYear;

    @Column(name = "DEAL_MONTH", nullable = false)
    private int dealMonth;
	
}
