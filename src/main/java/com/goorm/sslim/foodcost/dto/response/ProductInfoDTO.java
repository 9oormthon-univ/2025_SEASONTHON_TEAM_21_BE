package com.goorm.sslim.foodcost.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInfoDTO {


    @XmlElement(name = "goodId")
    private String goodId;

    @XmlElement(name = "goodName")
    private String goodName;

    @XmlElement(name = "productEntpCode")
    private String productEntpCode;

    @XmlElement(name = "goodUnitDivCode")
    private String goodUnitDivCode;

    @XmlElement(name = "goodBaseCnt")
    private String goodBaseCnt;

    @XmlElement(name = "goodSmlclsCode")
    private String goodSmlclsCode;

    @XmlElement(name = "detailMean")
    private String detailMean;

    @XmlElement(name = "goodTotalCnt")
    private String goodTotalCnt;

    @XmlElement(name = "goodTotalDivCode")
    private String goodTotalDivCode;
}
