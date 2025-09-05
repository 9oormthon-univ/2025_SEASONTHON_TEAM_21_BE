package com.goorm.sslim.housingCost.service;

import java.io.StringReader;
import java.time.YearMonth;
import java.util.List;
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

    /**
     * 호추될 모든 RTMS API들을 한 곳에서 정의
     * - path: 공공데이터포털 RTMS 경로
     * - type: 저장 시 구분용
     * 새 API가 생기면 여기 enum 상수만 추가하면 됨.
     */
    public enum HousingApi {
    	
        OFFICETEL("/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent", HousingType.OFFICETEL),
        APARTMENT("/RTMSDataSvcAptRent/getRTMSDataSvcAptRent", HousingType.APARTMENT),
        SINGLE("/RTMSDataSvcSHRent/getRTMSDataSvcSHRent", HousingType.SINGLE),
    	ROW_HOUSE("/RTMSDataSvcRHRent/getRTMSDataSvcRHRent", HousingType.ROW_HOUSE);

        private final String path;
        private final HousingType housingType;

        HousingApi(String path, HousingType housingType) {
            this.path = path;
            this.housingType = housingType;
        }

        public String path() {
            return path;
        }

        public HousingType housingType() {
            return housingType;
        }
        
    }

    public enum HousingType { OFFICETEL, APARTMENT, SINGLE, ROW_HOUSE }
    

    /** 오피스텔/아파트/단독·다가구 — 기간 호출 (region 테이블 전 구역 × 월 반복) **/
    // 오피스텔 호출
    public void fetchAndSaveYearlyOfficetel(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(HousingApi.OFFICETEL, start, end);
    }

    // 아파트 호출
    public void fetchAndSaveYearlyApartment(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(HousingApi.APARTMENT, start, end);
    }

    // 단독/다가구 호출
    public void fetchAndSaveYearlySingle(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(HousingApi.SINGLE, start, end);
    }
    
    // 연립다세대 호출
    public void fetchAndSaveYearlyRowHouse(YearMonth start, YearMonth end) {
        fetchAndSaveYearly(HousingApi.ROW_HOUSE, start, end);
    }

    /** 단건(특정 법정동 코드 × 특정 년월) 호출 **/
    // 오피스텔 호출
    public void fetchAndSaveOfficetelOnce(String lawdCd, String dealYmd) {
        fetchAndSaveOnce(HousingApi.OFFICETEL, lawdCd, dealYmd);
    }

    // 아파트 호출
    public void fetchAndSaveApartmentOnce(String lawdCd, String dealYmd) {
        fetchAndSaveOnce(HousingApi.APARTMENT, lawdCd, dealYmd);
    }

    // 단독/다가구 호출
    public void fetchAndSaveSingleOnce(String lawdCd, String dealYmd) {
        fetchAndSaveOnce(HousingApi.SINGLE, lawdCd, dealYmd);
    }
    
    // 연립다세대 호출
    public void fetchAndSaveRowHouseOnce(String lawdCd, String dealYmd) {
        fetchAndSaveOnce(HousingApi.ROW_HOUSE, lawdCd, dealYmd);
    }
    
    
    /* =========================
	    공통 내부 로직
	========================= */

	// region 테이블의 모든 LAWD_CD × [start..end] 월 반복
	private void fetchAndSaveYearly(HousingApi api, YearMonth start, YearMonth endInclusive) {
		
		final List<String> lawdCds = regionRepository.findAll().stream()
		       .map(Region::getLawdCd)
		       .filter(cd -> cd != null && !cd.isBlank())
		       .distinct()
		       .toList();
		
		final List<YearMonth> months = loopMonths(start, endInclusive);
		
		for (String lawdCd : lawdCds) {
		   for (YearMonth ym : months) {
		       final String dealYmd = String.format("%04d%02d", ym.getYear(), ym.getMonthValue());
		       fetchAndSaveOnce(api, lawdCd, dealYmd);
		   }
		}
			
	}
    
	// start~endInclusive까지 YearMonth 리스트 생성
    private List<YearMonth> loopMonths(YearMonth start, YearMonth endInclusive) {
    	
        final int months = (int) (endInclusive.getYear() * 12 + endInclusive.getMonthValue()
                - (start.getYear() * 12 + start.getMonthValue())) + 1;
        return IntStream.range(0, months)
                .mapToObj(start::plusMonths)
                .toList();
        
    }
    
    // 공통 단건 호출: API 요청 → XML 파싱 → DTO → Entity → 저장
    private void fetchAndSaveOnce(HousingApi api, String lawdCd, String dealYmd) {
    	
        final String xml = callRtmsApi(api.path(), lawdCd, dealYmd);
        if (xml == null || xml.isBlank()) {
            log.debug("[RTMS] 빈 응답: api={}, lawdCd={}, dealYmd={}", api.name(), lawdCd, dealYmd);
            return;
        }

        final List<HousingCostDto> dtoList = parseRtmsXml(xml);
        if (dtoList == null || dtoList.isEmpty()) return;

        // 공통 규칙:
        // - 전용면적 null이면 대체 필드(totalFloorAr) 사용
        // - 40㎡ 이하만
        // - 보증금/월세가 동시에 0이면 제외
        final List<HousingCost> entities = dtoList.stream()
                .map(dto -> {
                    final Double area = dto.getExclusiveArea() != null
                            ? dto.getExclusiveArea()
                            : dto.getTotalFloorAr();
                    if (area == null) return null; // 면적 자체가 없으면 스킵
                    if (area > 40.0) return null;

                    final double deposit = dto.getDeposit() != null ? dto.getDeposit() : 0.0;
                    final double monthly = dto.getMonthlyRent() != null ? dto.getMonthlyRent() : 0.0;
                    if (deposit == 0.0 && monthly == 0.0) return null;

                    return HousingCost.builder()
                            .sggNm(dto.getSggNm())          // 아파트는 sggNm이 비어올 수 있음(null 허용)
                            .sggCd(dto.getSggCd())          // 아파트는 sggCd를 우선 활용
                            .umdNm(dto.getUmdNm())
                            .deposit(deposit)
                            .monthlyRent(monthly)
                            .dealYear(dto.getDealYear())
                            .dealMonth(dto.getDealMonth())
                            .exclusiveArea(area)
                            .housingType(api.housingType())
                            .build();
                })
                .filter(e -> e != null)
                .toList();

        if (!entities.isEmpty()) {
            housingCostRepository.saveAll(entities);
            log.info("[RTMS] 저장 완료: api={}, lawdCd={}, dealYmd={}, rows={}",
                    api.name(), lawdCd, dealYmd, entities.size());
        }
        
    }
    
    // 실제 API 호출 (페이지가 필요하면 여기서 while 돌리면 됨)
    private String callRtmsApi(String path, String lawdCd, String dealYmd) {
    	
        try {
            return webClient.get()
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
        } catch (Exception e) {
            log.error("[RTMS] 호출 실패: path={}, lawdCd={}, dealYmd={}", path, lawdCd, dealYmd, e);
            return null;
        }
        
    }
    
    // XML → DTO 리스트
    private List<HousingCostDto> parseRtmsXml(String xml) {
    	
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Response response = (Response) unmarshaller.unmarshal(new StringReader(xml));

            if (response == null || response.getBody() == null ||
                response.getBody().getItems() == null ||
                response.getBody().getItems().getItem() == null) {
                return List.of();
            }
            return response.getBody().getItems().getItem();
        } catch (Exception e) {
            log.error("[RTMS] XML 파싱 오류", e);
            return List.of();
        }
        
    }
    
}