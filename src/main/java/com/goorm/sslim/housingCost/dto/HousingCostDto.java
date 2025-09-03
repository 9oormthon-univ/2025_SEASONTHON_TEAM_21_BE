package com.goorm.sslim.housingCost.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OfficetelRentDto {

	@XmlElement(name = "buildYear")
    private String buildYear; // 건축년도
    
    @XmlElement(name = "dealDay")
    private String dealDay; // 계약일
    
    @XmlElement(name = "dealMonth")
    private String dealMonth; // 계약월
    
    @XmlElement(name = "dealYear")
    private String dealYear; // 계약년도
    
    @XmlElement(name = "deposit")
    private String deposit; // 보증금액 (만원)
    
    @XmlElement(name = "excluUseAr")
    private double excluUseAr; // 전용면적
    
    @XmlElement(name = "monthlyRent")
    private String monthlyRent; // 월세금액 (만원)
    
    @XmlElement(name = "offiNm")
    private String offiNm; // 단지명
    
    @XmlElement(name = "sggCd")
    private String sggCd; // 지역코드
    
    @XmlElement(name = "sggNm")
    private String sggNm; // 시군구
    
    @XmlElement(name = "umdNm")
    private String umdNm; // 법정동
	
}
