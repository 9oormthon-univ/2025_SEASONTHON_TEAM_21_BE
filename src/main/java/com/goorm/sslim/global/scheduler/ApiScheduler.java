package com.goorm.sslim.global.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoDTO;
import com.goorm.sslim.housingCost.service.HousingCostService;
import com.goorm.sslim.region.service.RegionService;
import com.goorm.sslim.service.ProductInfoService;
import com.goorm.sslim.service.ProductPriceInfoService;
import com.goorm.sslim.service.PublicTransportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiScheduler {

	private final RegionService regionService;
	private final HousingCostService housingCostService;
    private final ProductInfoService productInfoService;
    private final ProductPriceInfoService productPriceInfoService;
    private final PublicTransportService publicTransportService;
    
    private final String[] necessaryItemIds = {"1206", "246", "874", "238", "1436"
            , "1263", "1598"};
    
    /**
     * 분기별 첫 금요일 새벽 3시 실행
     * 1,4,7,10월 → 분기 시작 월
     * "0 0 3 ? 1,4,7,10 FRI#1"
     *   - 초  분 시  일  월  요일
     *   - FRI#1 → 해당 월의 첫 번째 금요일
     */
    @Scheduled(cron = "0 0 3 ? 1,4,7,10 FRI#1")
    public void fetchQuarterlyApis() {
        log.info("===== 분기별 API 호출 시작 =====");

        try {
            log.info("▶ 국토부 지역코드 API 호출");
            regionService.syncAllLawdCdNationwide();
        } catch (Exception e) {
            log.error("국토부 지역코드 API 호출 실패", e);
        }
        
        try {
            log.info("▶ 국토부 실거래가 API 호출");
            YearMonth end = YearMonth.now();              // 현재 달
            YearMonth start = end.minusMonths(11);        // 1년 전
            
            housingCostService.fetchAndSaveYearlyOfficetel(start, end);
            housingCostService.fetchAndSaveYearlyApartment(start, end);
            housingCostService.fetchAndSaveYearlySingle(start, end);
            housingCostService.fetchAndSaveYearlyRowHouse(start, end);
        } catch (Exception e) {
            log.error("국토부 실거래가 API 호출 실패", e);
        }

        try {
            log.info("▶ 한국소비자원 ProductInfo API 호출");
            productInfoService.fetchProductInfo();
        } catch (Exception e) {
            log.error("ProductInfo API 호출 실패", e);
        }

        try {
            log.info("▶ 한국소비자원 ProductPrice API 호출 (기준일: 20250718)");

            String baseDate = "20250718";   // 고정값
            // 실행일 기준으로 날짜를 yyyyMMdd 형식으로 생성
            String fallbackDate = LocalDate.now()
                                           .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            for (String itemId : necessaryItemIds) {
                // 1차 호출
                List<ProductPriceInfoDTO> priceList = productPriceInfoService.fetchProductPriceInfo(baseDate, itemId);
                if (priceList == null || priceList.isEmpty()) {
                    log.warn("품목 {}: {} 데이터 없음 → 1주일 전({}) 재시도", itemId, baseDate, fallbackDate);

                    // 2차 호출
                    priceList = productPriceInfoService.fetchProductPriceInfo(fallbackDate, itemId);
                    if (priceList == null || priceList.isEmpty()) {
                        log.error("품목 {}: ProductPrice API 재시도 실패", itemId);
                        continue;
                    }
                    else {
                        productPriceInfoService.saveAveragePrice(fallbackDate, itemId);
                    }
                }
                else {
                    productPriceInfoService.saveAveragePrice(baseDate, itemId);
                }
            }
        } catch (Exception e) {
            log.error("ProductPrice API 호출 실패", e);
        }

        try {
            log.info("▶ 국토부 대중교통 API 호출");
            publicTransportService.ingestPivot();
        } catch (Exception e) {
            log.error("국토부 대중교통 API 호출 실패", e);
        }

        log.info("===== 분기별 API 호출 완료 =====");
        
    }
	
}
