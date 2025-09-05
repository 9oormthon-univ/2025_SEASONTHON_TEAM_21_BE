package com.goorm.sslim.housing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.goorm.sslim.housingCost.repository.HousingCostRepository;
import com.goorm.sslim.housingCost.service.HousingCostService;
import com.goorm.sslim.region.entity.Region;
import com.goorm.sslim.region.repository.RegionRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class HousingCostApiTest {

	@Autowired
    private HousingCostService housingCostService;

    @Autowired
    private HousingCostRepository housingCostRepository;
    
    @Autowired
    private RegionRepository regionRepository;
    
    @Autowired
    private Environment env;

    private static final String TEST_LAWD_CD = "11110"; // 종로구

    @BeforeEach
    void setupRegion() {
        if (regionRepository.count() == 0) {
            Region r = new Region();
            r.setLawdCd(TEST_LAWD_CD);   // 11110
            r.setSiName("서울특별시");      // ✅ 필수값 채우기
            r.setSggName("종로구");        // ✅ 필수값 채우기
            regionRepository.save(r);
        }
    }
    
    /**
     * 외부 API 키가 없거나 네트워크 환경이 아니면 테스트를 Skip
     * - 실제 API 호출을 수행하는 통합 테스트이므로 방어적으로 처리
     */
    private void assumeServiceKeyPresent() {
        String key = env.getProperty("apis.rtms.service-key");
        assumeTrue(key != null && !key.isBlank(),
                "apis.rtms.service-key 가 설정되지 않아 RTMS 통합 테스트를 건너뜁니다");
    }
    
    @Test
    @DisplayName("오피스텔 API 호출 → 응답 파싱 → 저장 검증")
    @Transactional
    void officetel_api_flow_success() {
        assumeServiceKeyPresent();

        long before = housingCostRepository.count();
        housingCostService.fetchAndSaveYearlyOfficetel(YearMonth.of(2024, 7), YearMonth.of(2024, 7));
        long after = housingCostRepository.count();

        assertThat(after).isGreaterThan(before);
    }

    @Test
    @DisplayName("아파트 API 호출 → 응답 파싱 → 저장 검증")
    @Transactional
    void apartment_api_flow_success() {
        assumeServiceKeyPresent();

        long before = housingCostRepository.count();
        housingCostService.fetchAndSaveYearlyApartment(YearMonth.of(2024, 7), YearMonth.of(2024, 7));
        long after = housingCostRepository.count();

        assertThat(after).isGreaterThan(before);
    }

    @Test
    @DisplayName("단독·다가구 API 호출 → 응답 파싱 → 저장 검증")
    @Transactional
    void single_api_flow_success() {
        assumeServiceKeyPresent();

        long before = housingCostRepository.count();
        housingCostService.fetchAndSaveYearlySingle(YearMonth.of(2024, 7), YearMonth.of(2024, 7));
        long after = housingCostRepository.count();

        assertThat(after).isGreaterThan(before);
    }

    @Test
    @DisplayName("연립·다세대 API 호출 → 응답 파싱 → 저장 검증")
    @Transactional
    void rowhouse_api_flow_success() {
        assumeServiceKeyPresent();

        long before = housingCostRepository.count();
        housingCostService.fetchAndSaveYearlyRowHouse(YearMonth.of(2024, 7), YearMonth.of(2024, 7));
        long after = housingCostRepository.count();

        assertThat(after).isGreaterThan(before);
    }

}
