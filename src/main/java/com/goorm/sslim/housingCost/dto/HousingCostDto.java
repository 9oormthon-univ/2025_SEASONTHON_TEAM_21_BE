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
	private Double exclusiveArea;   // 오피스텔/아파트

	@XmlElement(name = "totalFloorAr")
	private Double totalFloorAr;    // 단독/다가구
	
    @XmlElement(name = "deposit")
    private Double deposit;

    @XmlElement(name = "monthlyRent")
    private Double monthlyRent;

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
