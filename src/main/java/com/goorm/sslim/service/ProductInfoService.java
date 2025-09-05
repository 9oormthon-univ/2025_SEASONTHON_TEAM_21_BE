package com.goorm.sslim.service;

import com.goorm.sslim.foodcost.dto.response.ProductInfoDTO;
import com.goorm.sslim.foodcost.dto.response.ProductInfoResponse;
import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoDTO;
import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoResponse;
import com.goorm.sslim.global.code.ErrorCode;
import com.goorm.sslim.global.exception.GeneralException;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoService {

    @Value("${spring.openapi.product-price.service-key}")
    private String serviceKey;
    private static final String BASE_URL =
            "http://openapi.price.go.kr/openApiImpl/ProductPriceInfoService/getProductInfoSvc.do";
    Map<String, String> goodIdNameMap;

    @PostConstruct  // 앱 시작 후 한 번 실행
    public void init() throws JAXBException {
        fetchProductInfo();
        log.info("Product map initialized with {} items", goodIdNameMap.size());
    }

    public List<ProductInfoDTO> fetchProductInfo() throws JAXBException {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("ServiceKey", serviceKey)
                .toUriString();

        // 1. RestTemplate으로 XML String 받기
        RestTemplate restTemplate = new RestTemplate();
        String xmlResponse = restTemplate.getForObject(url, String.class);
        if(xmlResponse == null) {
            throw new GeneralException(ErrorCode._NULL_API);
        }

        // 2. JAXB로 XML -> DTO List로 변환
        JAXBContext jaxbContext = JAXBContext.newInstance(ProductInfoResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        ProductInfoResponse response =
                (ProductInfoResponse) unmarshaller.unmarshal(new StringReader(xmlResponse));

        goodIdNameMap = response.getResult().getItems().stream()
                .collect(Collectors.toMap(ProductInfoDTO::getGoodId, ProductInfoDTO::getGoodName));

        return response.getResult().getItems();
    }

    public String findGoodName(String goodId) {
        return goodIdNameMap.get(goodId);
    }
}
