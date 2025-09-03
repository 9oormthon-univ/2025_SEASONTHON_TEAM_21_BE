package com.goorm.sslim.service;

import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoDTO;
import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoResponse;
import com.goorm.sslim.foodcost.repository.FoodCostRepository;
import com.goorm.sslim.global.code.ErrorCode;
import com.goorm.sslim.global.exception.GeneralException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPriceInfoService {

    @Value("${spring.openapi.product-price.service-key}")
    private String serviceKey;
    private static final String BASE_URL =
            "http://openapi.price.go.kr/openApiImpl/ProductPriceInfoService/getProductPriceInfoSvc.do";
    private final FoodCostRepository foodCostRepository;

    public List<ProductPriceInfoDTO> fetchProductPriceInfo(String goodInspectDay, String goodId) throws JAXBException {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("goodInspectDay", goodInspectDay)
                .queryParam("goodId", goodId)
                .queryParam("ServiceKey", serviceKey)
                .toUriString();

        // 1. RestTemplate으로 XML String 받기
        RestTemplate restTemplate = new RestTemplate();
        String xmlResponse = restTemplate.getForObject(url, String.class);
        if(xmlResponse == null) {
            throw new GeneralException(ErrorCode._NULL_API);
        }

        // 2. JAXB로 XML -> DTO List로 변환
        JAXBContext jaxbContext = JAXBContext.newInstance(ProductPriceInfoResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ProductPriceInfoResponse response =
                (ProductPriceInfoResponse) unmarshaller.unmarshal(new StringReader(xmlResponse));

        return response.getItems();
    }
}
