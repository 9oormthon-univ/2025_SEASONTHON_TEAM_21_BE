package com.goorm.sslim.region;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.goorm.sslim.region.entity.Region;
import com.goorm.sslim.region.repository.RegionRepository;
import com.goorm.sslim.region.service.RegionService;

import jakarta.transaction.Transactional;

@SpringBootTest
public class RegionApiTest {

	@Autowired
    private RegionService regionService;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private Environment env;
	
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
    @DisplayName("지역코드 API 호출 → 응답 파싱 → DB 저장")
    void regionalCode_api_flow_success() {
    	
    	assumeServiceKeyPresent();

        long before = regionRepository.count();

        // 실제 서비스 호출
        regionService.syncAllLawdCdNationwide();

        long after = regionRepository.count();

        // 저장 결과 검증
        assertThat(after).isGreaterThan(before)
            .withFailMessage("지역코드 API 호출 후 DB에 데이터가 저장되지 않았습니다.");

        // 전체 건수와 일부 결과를 콘솔에 출력
        System.out.println("===== REGION 저장 결과 =====");
        System.out.println("Before count = " + before);
        System.out.println("After count  = " + after);

        // 저장된 엔트리 일부를 확인 (최대 5건)
        List<Region> all = regionRepository.findAll();
        all.stream().limit(5).forEach(r -> 
            System.out.printf("lawdCd=%s, siName=%s, sggName=%s%n",
                r.getLawdCd(), r.getSiName(), r.getSggName())
        );
        System.out.println("===========================");

        // 첫 번째 엔트리 값 간단 검증
        var first = all.get(0);
        assertThat(first.getLawdCd()).hasSize(5);
        assertThat(first.getSiName()).isNotBlank();
        assertThat(first.getSggName()).isNotBlank();
        
    }
    
}
