package com.goorm.sslim.income.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.sslim.income.entity.Income;
import com.goorm.sslim.income.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository repository;
    
    @Value("${custom.api.income-key}")
    private String apiKey;
    
    private static final Map<Integer, String> TOP_PERCENT_TO_GROUP = Map.of(
            80,  "1분위",
            60,  "2분위",
            40,  "3분위",
            20,  "4분위",
            0, "5분위"
    );

    private static final Pattern TOP_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)%");
    //private static final Pattern TOP_PATTERN = Pattern.compile("상위\\s*(\\d+(?:\\.\\d+)?)%");

    public void fetchAndSaveIncomeBoundaries() {
    	
        String url = "https://api.odcloud.kr/api/15082063/v1/uddi:f54973ac-a43f-4bcd-bc0c-99b91e1b0fde"
                + "?page=1"
                + "&perPage=1000"      // 넉넉히 요청
                + "&returnType=JSON"
                + "&serviceKey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) return;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode items = root.path("data");

            // 중복 삽입 방지 & 마지막에 정렬/저장 편의를 위해 맵으로 수집
            Map<String, Income> pick = new HashMap<>();

            for (JsonNode item : items) {
                String groupStr = item.path("구분").asText(null);
                if (groupStr == null) continue;

                // "상위 X%"에서 X 숫자 추출
                Matcher m = TOP_PATTERN.matcher(groupStr);
                if (!m.find()) continue;

                double topPercentDouble = Double.parseDouble(m.group(1));
                int topPercentRounded = (int) Math.round(topPercentDouble);

                // 우리가 원하는 상위 % (100,80,60,40,20) 인지 체크
                String quintile = TOP_PERCENT_TO_GROUP.get(topPercentRounded);
                if (quintile == null) continue;

                // 근로소득금액 (억 원 단위) → 원 단위 변환
                long laborIncomeEok = item.path("총급여").asLong();
                long person = item.path("인원").asLong();
                long result = laborIncomeEok * 10000 / person /12 ;


                Income e = new Income();
                e.setYear(String.valueOf(Year.now().getValue()));
                e.setGroup(quintile);
                e.setBoundary(result);

                pick.putIfAbsent(quintile, e);
            }

            // 저장 순서를 1~5분위로 정렬
            List<String> order = List.of("1분위", "2분위", "3분위", "4분위", "5분위");
            List<Income> entities = new ArrayList<>();
            for (String g : order) {
                if (pick.containsKey(g)) {
                    entities.add(pick.get(g));
                }
            }

            repository.saveAll(entities);

        } catch (Exception e) {
            throw new RuntimeException("API 응답 파싱/가공 실패", e);
        }
        
    }
    
}
