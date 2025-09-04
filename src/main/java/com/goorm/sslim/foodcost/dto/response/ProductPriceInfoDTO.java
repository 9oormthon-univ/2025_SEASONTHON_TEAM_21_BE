package com.goorm.sslim.foodcost.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductPriceInfoDTO {

    @XmlElement(name = "goodInspectDay")
    private String goodInspectDay;

    @XmlElement(name = "entpId")
    private String entpId;

    @XmlElement(name = "goodId")
    private String goodId;

    @XmlElement(name = "goodPrice")
    private String goodPrice;

    @XmlElement(name = "inputDttm")
    private String inputDttm;
}
