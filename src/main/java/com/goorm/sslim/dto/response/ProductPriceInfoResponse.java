package com.goorm.sslim.dto.response;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

import java.util.List;

@Getter
@XmlRootElement(name = "result")
public class ProductPriceInfoResponse {

    @XmlElement
    private List<ProductPriceInfoDTO> items;
}
