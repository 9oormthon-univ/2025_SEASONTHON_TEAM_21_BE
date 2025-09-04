package com.goorm.sslim.housingCost.service;

import java.io.StringReader;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.goorm.sslim.housingCost.dto.HousingCostDto;
import com.goorm.sslim.housingCost.entity.HousingCost;
import com.goorm.sslim.housingCost.repository.HousingCostRepository;
import com.goorm.sslim.housingCost.xml.Response;
import com.goorm.sslim.region.entity.Region;
import com.goorm.sslim.region.repository.RegionRepository;

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
    private final RegionRepository regionRepository;

    @Value("${apis.rtms.service-key}")
    private String serviceKey;

    // 오피스텔
    private static final String PATH_OFFICETEL = "/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent";
    
    // 아파트
    private static final String PATH_APARTMENT = "/RTMSDataSvcAptRent/getRTMSDataSvcAptRent";
    
    // 연립/단독 주택
    private static final String PATH_ROWHOUSE  = "/RTMSDataSvcRowHouseRent/getRTMSDataSvcRowHouseRent";

    // 오피스텔 호출
    public void fetchAndSaveOfficetelYearly(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(PATH_OFFICETEL, start, end, HousingType.OFFICETEL);
    }

    // 아파트 호출
    public void fetchAndSaveApartmentYearly(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(PATH_APARTMENT, start, end, HousingType.APARTMENT);
    }

    // 연립/단독 호출
    public void fetchAndSaveRowhouseYearly(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(PATH_ROWHOUSE, start, end, HousingType.ROWHOUSE);
    }
    
    // 공통 로직: region 테이블의 모든 lawdCd × YearMonth 구간 반복
    private void fetchAndSaveYearly(String path, YearMonth start, YearMonth end, HousingType type) {
    	
        List<String> lawdCds = regionRepository.findAll().stream()
                .map(Region::getLawdCd)
                .filter(cd -> cd != null && !cd.isBlank())
                .distinct()
                .toList();

        for (String lawdCd : lawdCds) {
            for (YearMonth ym : loopMonths(start, end)) {
                String dealYmd = String.format("%04d%02d", ym.getYear(), ym.getMonthValue());
                fetchAndSave(path, lawdCd, dealYmd, type);
            }
        }
        
    }
    
    // start~endInclusive까지 YearMonth 리스트 생성
    private List<YearMonth> loopMonths(YearMonth start, YearMonth endInclusive) {
        int months = (int) (endInclusive.getYear() * 12 + endInclusive.getMonthValue()
                - (start.getYear() * 12 + start.getMonthValue())) + 1;
        return IntStream.range(0, months)
                .mapToObj(start::plusMonths)
                .toList();
    }
    
    // 단건 호출: API 요청 → XML 응답 파싱 → DB 저장
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
        	log.error("XML 파싱 오류", e);
            return;
        }

        if (response == null || response.getBody() == null ||
            response.getBody().getItems() == null ||
            response.getBody().getItems().getItem() == null) {
            return;
        }
        
        // 3) DTO → Entity 매핑
        List<HousingCostDto> dtoList = response.getBody().getItems().getItem();

        List<HousingCost> entities = dtoList.stream()
        		// 전용면적 40㎡ 이하만
                .filter(dto -> dto.getExclusiveArea() <= 40.0)
                
                // 보증금/월세가 동시에 0인 경우 제외
                .filter(dto -> !(dto.getDeposit() == 0.0 && dto.getMonthlyRent() == 0.0))
                
                // 매핑
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
        if (!entities.isEmpty()) {
            housingCostRepository.saveAll(entities);
        }
        
    }
    

    public enum HousingType { OFFICETEL, APARTMENT, ROWHOUSE }
    
}