package com.goorm.sslim.housing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.goorm.sslim.housingCost.entity.HousingCost;
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

    @BeforeEach
    void setupRegion() {
        // Given: region 테이블에 테스트용 LAWD_CD 등록 (예: 종로구 11110)
        // NOTE: Region 엔티티의 NOT NULL 컬럼이 있다면 여기에 함께 채워주세요.
        if (regionRepository.count() == 0) {
            Region r = new Region();
            r.setLawdCd("11110"); // 종로구
            regionRepository.save(r);
        }
    }
    
    @Test
    @DisplayName("오피스텔 API → region.lawdCd×월 구간 → 파싱/저장 검증")
    @Transactional  // 테스트 후 롤백
    void fetchAndSave_officetel_yearly_success() {
    	
        // Given
        long beforeCount = housingCostRepository.count();
        YearMonth from = YearMonth.of(2024, 7);
        YearMonth to   = YearMonth.of(2024, 7); // 속도 위해 1개월만. 필요하면 12월로 변경

        // When
        housingCostService.fetchAndSaveOfficetelYearly(from, to);

        // Then
        List<HousingCost> saved = housingCostRepository.findAll();
        long afterCount = saved.size();
        assertThat(afterCount).isGreaterThan(beforeCount);

        // 샘플 출력 및 무결성 간단 검증
        saved.stream().limit(5).forEach(h -> {
            System.out.println("[OFFICETEL] " + h.getSggNm() + " / " + h.getUmdNm()
                    + " / area=" + h.getExclusiveArea()
                    + " / deposit=" + h.getDeposit()
                    + " / rent=" + h.getMonthlyRent()
                    + " / " + h.getDealYear() + "-" + h.getDealMonth());
        });

        assertThat(saved).allSatisfy(h -> {
            assertThat(h.getDealYear()).isNotZero();
            assertThat(h.getDealMonth()).isNotZero();
            assertThat(h.getSggCd()).isNull();
            assertThat(h.getSggNm()).isNotBlank();
            assertThat(h.getUmdNm()).isNotBlank();
            assertThat(h.getHousingType().name()).isEqualTo("OFFICETEL");
        });
        
    }
    
    
    @Test
    @DisplayName("아파트 API → region.lawdCd×월 구간 → 파싱/저장 검증")
    @Transactional  // 테스트 후 롤백
    void fetchAndSave_apartment_yearly_success() {
    	
        // Given
        long beforeCount = housingCostRepository.count();
        YearMonth from = YearMonth.of(2024, 7);
        YearMonth to   = YearMonth.of(2024, 9); // 속도 위해 1개월만

        // When
        housingCostService.fetchAndSaveApartmentYearly(from, to);

        // Then
        List<HousingCost> saved = housingCostRepository.findAll();
        long afterCount = saved.size();
        assertThat(afterCount).isGreaterThan(beforeCount);

        saved.stream().limit(5).forEach(h -> {
            System.out.println("[APARTMENT] " + h.getSggNm() + " / " + h.getUmdNm()
                    + " / area=" + h.getExclusiveArea()
                    + " / deposit=" + h.getDeposit()
                    + " / rent=" + h.getMonthlyRent()
                    + " / " + h.getDealYear() + "-" + h.getDealMonth());
        });

        assertThat(saved).allSatisfy(h -> {
            assertThat(h.getDealYear()).isNotZero();
            assertThat(h.getDealMonth()).isNotZero();
            assertThat(h.getSggCd()).isNotBlank();
            assertThat(h.getSggNm()).isNull();
            assertThat(h.getUmdNm()).isNotBlank();
            assertThat(h.getHousingType().name()).isEqualTo("APARTMENT");
        });
        
    }
	
}
