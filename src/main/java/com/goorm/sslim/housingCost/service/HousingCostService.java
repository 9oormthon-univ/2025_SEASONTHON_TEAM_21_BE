package com.goorm.sslim.housingCost.service;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.goorm.sslim.housingCost.dto.HousingCostDto;
import com.goorm.sslim.housingCost.dto.OfficetelRentDto;
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
    private static final String API_URL = "http://apis.data.go.kr/1613000/RTMSDataSvcOffiRent";
    
    // API 키 추후 환경변수로 변환
    private final String officetelKey = "2d4ea1d47d8bb9f264dac2c60c50527ce80b29540db5eaaa1e026f22f629e9de";

    public List<HousingCostDto> getOfficetelRentData(String lawdCd, String dealYmd) {
    	
        String uri = "/getRTMSDataSvcOffiRent?serviceKey={serviceKey}&LAWD_CD={lawdCd}&DEAL_YMD={dealYmd}";
        
        try {
            String xmlResponse = webClient.get()
                .uri(uri, officetelKey, lawdCd, dealYmd)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            // API 응답이 에러 메시지인 경우를 먼저 체크합니다.
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
                .filter(item -> item.getExcluUseAr() <= 40.0)	// 전용면적 40㎡ 이하 필터링
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
    
//    public void fetchAndSaveOfficetelData(String lawdCd, String dealYmd) {
//    	
//        // 1. 오피스텔 API 호출 및 데이터 파싱
//        List<HousingCostDto> officetelDataList = getOfficetelRentData(lawdCd, dealYmd);
//
//        // 2. 파싱된 데이터를 DB에 저장
//        if (!officetelDataList.isEmpty()) {
//            System.out.println("성공적으로 파싱된 오피스텔 데이터: " + officetelDataList.size() + "건");
//        } else {
//            System.out.println("API 호출 결과 데이터가 없거나 파싱에 실패했습니다.");
//        }
//        
//    }
    
}