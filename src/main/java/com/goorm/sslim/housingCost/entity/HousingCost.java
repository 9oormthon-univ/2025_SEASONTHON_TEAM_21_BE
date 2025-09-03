package com.goorm.sslim.housingCost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HOUSING_COST")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousingCost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long housingCostId;
	
	private Long regionId;
    
    private String housingType;
    
    private double exclusiveArea;
    
    private double deposit;
    
    private double monthlyRent;
    
    private String sggNm;
    
    private String umdNm;
    
    private int dealYear;
    
    private int dealMonth;
	
    
    
}
