package com.goorm.sslim.housingCost.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.goorm.sslim.housingCost.dto.HousingCostDto;
import com.goorm.sslim.housingCost.dto.HousingCostDto;
import com.goorm.sslim.housingCost.entity.HousingCost;
import com.goorm.sslim.housingCost.repository.HousingCostRepository;
import com.goorm.sslim.housingCost.xml.Body;
import com.goorm.sslim.housingCost.xml.Items;
import com.goorm.sslim.housingCost.xml.Response;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HousingCostService {
	
	private final WebClient webClient;
    private final HousingCostRepository housingCostRepository;

    // 환경변수에서 주입
    @Value("${apis.rtms.service-key}")
    private String serviceKey;

    private static final int NUM_OF_ROWS = 200;
    private static final double CONVERSION_RATE = 0.025; // 전월세 환산율 2.5%

    // 오피스텔
    private static final String PATH_OFFICETEL = "/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent";
    
    // 아파트
    private static final String PATH_APARTMENT = "/RTMSDataSvcAptRent/getRTMSDataSvcAptRent";
    
    // 연립/단독 주택
    private static final String PATH_ROWHOUSE  = "/RTMSDataSvcRowHouseRent/getRTMSDataSvcRowHouseRent";

    // 오피스텔 호출
    public void fetchAndSaveOfficetel(String lawdCd, String dealYmd) {
        fetchAndSave(PATH_OFFICETEL, lawdCd, dealYmd, HousingType.OFFICETEL);
    }

    // 아파트 호출
    public void fetchAndSaveApartment(String lawdCd, String dealYmd) {
        fetchAndSave(PATH_APARTMENT, lawdCd, dealYmd, HousingType.APARTMENT);
    }

    // 연립/단독 호출
    public void fetchAndSaveRowhouse(String lawdCd, String dealYmd) {
        fetchAndSave(PATH_ROWHOUSE, lawdCd, dealYmd, HousingType.ROWHOUSE);
    }

    private void fetchAndSave(String path, String lawdCd, String dealYmd, HousingType type) {
    	
    	// 1) API 호출 (XML 응답 받기)
        String xml = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("LAWD_CD", lawdCd)
                        .queryParam("DEAL_YMD", dealYmd)
                        .queryParam("numOfRows", 1000)
                        .queryParam("pageNo", 1)
                        .build())
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (xml == null || xml.isBlank()) return;
        
        // 2) XML → 객체 변환
        Response response;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            response = (Response) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (response == null || response.getBody() == null ||
            response.getBody().getItems() == null ||
            response.getBody().getItems().getItem() == null) {
            return;
        }
        
        // 3) DTO → Entity 매핑 (그대로 저장)
        List<HousingCostDto> dtoList = response.getBody().getItems().getItem();

        List<HousingCost> entities = dtoList.stream()
        		// 1. 전용면적 40㎡ 이하만
                .filter(dto -> dto.getExclusiveArea() <= 40.0)
                
                // 2. 보증금/월세가 동시에 0인 경우 제외
                .filter(dto -> !(dto.getDeposit() == 0.0 && dto.getMonthlyRent() == 0.0))
                
                // 3. 매핑
                .map(dto -> HousingCost.builder()
                        .sggNm(dto.getSggNm())
                        .sggCd(dto.getSggCd())
                        .umdNm(dto.getUmdNm())
                        .deposit(dto.getDeposit())
                        .monthlyRent(dto.getMonthlyRent())
                        .dealYear(dto.getDealYear())
                        .dealMonth(dto.getDealMonth())
                        .exclusiveArea(dto.getExclusiveArea())
                        .housingType(type)
                        .build())
                .collect(Collectors.toList());

        // 4) DB 저장
        housingCostRepository.saveAll(entities);
        
    }
    

    public enum HousingType { OFFICETEL, APARTMENT, ROWHOUSE }
    
}