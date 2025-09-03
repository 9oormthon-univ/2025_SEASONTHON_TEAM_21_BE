package com.goorm.sslim.housingCost.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousingCostDto {

	@XmlElement(name = "excluUseAr")
    private double exclusiveArea;

    @XmlElement(name = "deposit")
    private double deposit;

    @XmlElement(name = "monthlyRent")
    private double monthlyRent;

    @XmlElement(name = "sggNm")
    private String sggNm;
    
    @XmlElement(name = "sggCd")
    private String sggCd;

    @XmlElement(name = "umdNm")
    private String umdNm;

    @XmlElement(name = "dealYear")
    private int dealYear;

    @XmlElement(name = "dealMonth")
    private int dealMonth;
	
}
