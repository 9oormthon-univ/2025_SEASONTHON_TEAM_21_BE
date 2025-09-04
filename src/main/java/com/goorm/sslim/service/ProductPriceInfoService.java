package com.goorm.sslim.service;

import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoDTO;
import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoResponse;
import com.goorm.sslim.foodcost.entity.FoodCost;
import com.goorm.sslim.foodcost.repository.FoodCostRepository;
import com.goorm.sslim.global.code.ErrorCode;
import com.goorm.sslim.global.exception.GeneralException;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

        return response.getResult().getItems();
    }

    @Transactional
    public void calculateAndSaveAverage(String goodInspectDay, String goodId) throws JAXBException {
        List<ProductPriceInfoDTO> priceList = fetchProductPriceInfo(goodInspectDay, goodId);

        if (priceList == null || priceList.isEmpty()) {
            log.warn("No price data found for goodId: {}", goodId);
            return;  // 데이터 없으면 저장 스킵
        }

        // 평균 계산 (Stream API 사용, goodPrice를 Double로 변환)
        double average = priceList.stream()
                .filter(dto -> dto.getGoodPrice() != null)  // null 가격 필터
                .mapToDouble(dto -> Double.parseDouble(dto.getGoodPrice()))  // 문자열 → double 변환
                .average()  // 평균 계산
                .orElse(0);  // 데이터 없으면 0

        log.info("Calculated average price for goodId {}: {}", goodId, average);

        // DB 저장
        FoodCost foodCost = FoodCost.builder()
                .Id(goodId)
                .goodName(findGoodName(goodId))
                .avgGoodPrice(Math.round(average * 10D) / 10D)
                .goodInspectDay(goodInspectDay)
                .build();


        foodCostRepository.save(foodCost);
    }

    public String findGoodName(String goodId) {
        String goodName = "default";

        return goodName;
    }
}
