package com.goorm.sslim.housingCost.service;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.goorm.sslim.housingCost.dto.HousingCostDto;
import com.goorm.sslim.housingCost.dto.OfficetelRentDto;
import com.goorm.sslim.housingCost.entity.HousingCost;
import com.goorm.sslim.housingCost.repository.HousingCostRepository;
import com.goorm.sslim.housingCost.xml.Body;
import com.goorm.sslim.housingCost.xml.Items;
import com.goorm.sslim.housingCost.xml.Response;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HousingCostService {
	
	private final WebClient webClient;
	private final HousingCostRepository housingCostRepository;
    
    @Value("${custom.api.officetel-key}")
    private String officetelKey;
    
    private static final String API_PATH = "/getRTMSDataSvcOffiRent?&LAWD_CD={lawdCd}&DEAL_YMD={dealYmd}&serviceKey={serviceKey}";

    public List<HousingCostDto> getOfficetelRentData(String lawdCd, String dealYmd) {

        try {
            String xmlResponse = webClient.get()
                .uri(API_PATH, lawdCd, dealYmd, officetelKey)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (xmlResponse.contains("<OpenAPI_ServiceResponse>")) {
                System.err.println("API 호출 실패: 유효하지 않은 API 키, IP 주소 불일치 또는 잘못된 요청입니다.");
                System.err.println("--- API 서버 응답 ---\n" + xmlResponse);
                return Collections.emptyList();
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Response response = (Response) unmarshaller.unmarshal(new StringReader(xmlResponse));

            List<OfficetelRentDto> officetelData = Optional.ofNullable(response.getBody())
                .map(Body::getItems)
                .map(Items::getItem)
                .orElse(Collections.emptyList());

            return officetelData.stream()
                .filter(item -> item.getExcluUseAr() <= 40.0)
                .map(item -> HousingCostDto.builder()
                        .housingType("Officetel")
                        .exclusiveArea(item.getExcluUseAr())
                        .deposit(Double.parseDouble(item.getDeposit().replace(",", "")))
                        .monthlyRent(Double.parseDouble(item.getMonthlyRent().replace(",", "")))
                        .sggNm(item.getSggNm())
                        .umdNm(item.getUmdNm())
                        .dealYear(Integer.parseInt(item.getDealYear()))
                        .dealMonth(Integer.parseInt(item.getDealMonth()))
                        .build())
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("오피스텔 API 호출 중 오류 발생: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public void fetchAndSaveOfficetelData(String lawdCd, String dealYmd) {
    	
	    List<HousingCostDto> officetelDataList = getOfficetelRentData(lawdCd, dealYmd);
	
	    if (!officetelDataList.isEmpty()) {
	        List<HousingCost> housingCosts = officetelDataList.stream()
	                .map(dto -> {
	                    HousingCost entity = new HousingCost();
	                    entity.setHousingType(dto.getHousingType());
	                    entity.setExclusiveArea(dto.getExclusiveArea());
	                    entity.setDeposit(dto.getDeposit());
	                    entity.setMonthlyRent(dto.getMonthlyRent());
	                    entity.setSggNm(dto.getSggNm());
	                    entity.setUmdNm(dto.getUmdNm());
	                    entity.setDealYear(dto.getDealYear());
	                    entity.setDealMonth(dto.getDealMonth());
	                    return entity;
	                })
	                .collect(Collectors.toList());
	
	        housingCostRepository.saveAll(housingCosts);
	        System.out.println("성공적으로 " + housingCosts.size() + "건의 오피스텔 데이터가 저장되었습니다.");
	        
	    } else {
	        System.out.println("저장할 데이터가 없습니다.");
	    }
    
    }
    
}