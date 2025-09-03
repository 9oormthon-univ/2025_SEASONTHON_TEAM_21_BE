package com.goorm.sslim.housing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.goorm.sslim.housingCost.entity.HousingCost;
import com.goorm.sslim.housingCost.repository.HousingCostRepository;
import com.goorm.sslim.housingCost.service.HousingCostService;

import jakarta.transaction.Transactional;

@SpringBootTest
public class HousingCostApiTest {

	@Autowired
    private HousingCostService housingCostService;

    @Autowired
    private HousingCostRepository housingCostRepository;

    @Test
    @DisplayName("오피스텔 API → 응답 파싱 → DB 저장까지 정상 동작 확인")
    @Transactional // 테스트 끝나면 자동 롤백 (DB 깨끗하게 유지)
    void fetchAndSave_officetel_success() {
        // given
        String lawdCd = "11110";  // 종로구
        String dealYmd = "202407"; // 2024년 7월

        long beforeCount = housingCostRepository.count();

        // when
        housingCostService.fetchAndSaveOfficetel(lawdCd, dealYmd);

        // then
        List<HousingCost> saved = housingCostRepository.findAll();
        long afterCount = saved.size();

        System.out.println("===== DB 저장 결과 =====");
        System.out.println("저장 전 건수: " + beforeCount);
        System.out.println("저장 후 건수: " + afterCount);
        assertThat(afterCount).isGreaterThan(beforeCount);

        // 샘플 5건 출력
        System.out.println("===== 샘플 데이터 (상위 5건) =====");
        for (int i = 0; i < Math.min(5, saved.size()); i++) {
            HousingCost h = saved.get(i);
            System.out.println("[" + (i + 1) + "]");
            System.out.println("  시군구: " + h.getSggNm());
            System.out.println("  법정동: " + h.getUmdNm());
            System.out.println("  전용면적: " + h.getExclusiveArea());
            System.out.println("  보증금: " + h.getDeposit());
            System.out.println("  월세: " + h.getMonthlyRent());
            System.out.println("  계약년도: " + h.getDealYear());
            System.out.println("  계약월: " + h.getDealMonth());
            System.out.println("  주거형태: " + h.getHousingType());
        }

        // 무결성 검증
        assertThat(saved)
                .allSatisfy(h -> {
                    assertThat(h.getDealYear()).isNotZero();
                    assertThat(h.getDealMonth()).isNotZero();
                    assertThat(h.getSggNm()).isNotBlank();
                    assertThat(h.getUmdNm()).isNotBlank();
                    assertThat(h.getHousingType().name()).isEqualTo("OFFICETEL");
                });
    }
    
    @Test
    @DisplayName("아파트 API → 응답 파싱 → DB 저장까지 정상 동작 확인")
    @Transactional // 테스트 후 DB 롤백
    void fetchAndSave_apartment_success() {
        // given
        String lawdCd = "11110";   // 종로구
        String dealYmd = "202407"; // 2024년 7월 데이터

        long before = housingCostRepository.count();

        // when
        housingCostService.fetchAndSaveApartment(lawdCd, dealYmd);

        // then
        long after = housingCostRepository.count();
        System.out.println("저장 전: " + before + ", 저장 후: " + after);

        List<HousingCost> saved = housingCostRepository.findAll();

        // 샘플 출력
        saved.stream().limit(5).forEach(h -> {
            System.out.println("=== 아파트 데이터 ===");
            System.out.println("시군구: " + h.getSggNm());
            System.out.println("시군구 코드: " + h.getSggCd());
            System.out.println("법정동: " + h.getUmdNm());
            System.out.println("전용면적: " + h.getExclusiveArea());
            System.out.println("보증금: " + h.getDeposit());
            System.out.println("월세: " + h.getMonthlyRent());
            System.out.println("계약년도: " + h.getDealYear());
            System.out.println("계약월: " + h.getDealMonth());
            System.out.println("주거형태: " + h.getHousingType());
        });

        // 간단 검증
        assertThat(after).isGreaterThan(before);
    }
	
}
