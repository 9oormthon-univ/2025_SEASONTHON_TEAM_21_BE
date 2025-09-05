package com.goorm.sslim.foodcost.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
public class ProductInfoResponse {

    @XmlElement(name = "result")
    private Result result;

    @XmlAccessorType(XmlAccessType.FIELD)
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Result {

        @XmlElement(name = "item")
        private List<ProductInfoDTO> items;
    }
}