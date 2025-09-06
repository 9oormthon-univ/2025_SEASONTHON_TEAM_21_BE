package com.goorm.sslim.householdExpense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.goorm.sslim.householdExpense.entity.AgeGroup;
import com.goorm.sslim.householdExpense.entity.HouseholdExpense;
import com.goorm.sslim.householdExpense.repository.HouseholdExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseholdExpenseIngestService {

    private final HouseholdExpenseRepository repo;

    private final WebClient web = WebClient.builder()
            .baseUrl("https://kosis.kr")
            // text/html로 내려와도 String으로 먼저 받기 때문에 OK
            .defaultHeader("Accept", MediaType.ALL_VALUE)
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 관심 연령(그 외는 스킵)
    private static final Set<String> TARGET_AGE_LABELS = Set.of(
            "만19세-24세", "만25세-29세", "만30세-34세"
    );

    // 카테고리 키
    private enum CatKey { FOOD, RENT_MONTHLY, HOUSING_MGMT, EDUCATION, TELECOM, TRANSPORT, RECREATION }

    // C2_NM → CatKey 매핑
    private static Optional<CatKey> mapCategory(String c2) {
        if (c2 == null) return Optional.empty();
        return switch (c2) {
            case "식료품비" -> Optional.of(CatKey.FOOD);
            case "주거비(월세)" -> Optional.of(CatKey.RENT_MONTHLY);
            case "주거비(주거 관리비)" -> Optional.of(CatKey.HOUSING_MGMT);
            case "교육비" -> Optional.of(CatKey.EDUCATION);
            case "통신비" -> Optional.of(CatKey.TELECOM);
            case "교통비" -> Optional.of(CatKey.TRANSPORT);
            case "오락·문화비" -> Optional.of(CatKey.RECREATION);
            default -> Optional.empty();
        };
    }

    // C1_NM → AgeGroup
    private static Optional<AgeGroup> toAgeGroup(String c1) {
        if (c1 == null) return Optional.empty();
        return switch (c1) {
            case "만19세-24세" -> Optional.of(AgeGroup.LOW);
            case "만25세-29세" -> Optional.of(AgeGroup.MIDDLE);
            case "만30세-34세" -> Optional.of(AgeGroup.HIGH);
            default -> Optional.empty();
        };
    }

    // 숫자 파싱(콤마 제거)
    private static Integer parseInt(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replace(",", "").trim();
        if (cleaned.isEmpty()) return null;
        try {
            return Integer.valueOf(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 평균 계산용 누적기
    private static class Acc {
        Map<CatKey, Long> sum = new EnumMap<>(CatKey.class);
        Map<CatKey, Integer> cnt = new EnumMap<>(CatKey.class);

        void add(CatKey k, Integer v) {
            if (k == null || v == null) return;
            sum.merge(k, v.longValue(), Long::sum);
            cnt.merge(k, 1, Integer::sum);
        }

        Integer avg(CatKey k) {
            Long s = sum.get(k);
            Integer c = cnt.get(k);
            if (s == null || c == null || c == 0) return null;
            return (int) Math.round(s.doubleValue() / c);
        }
    }

    /**
     * KOSIS에서 최신 1개 기간 데이터를 조회해
     * C1_NM ∈ {만19세-24세, 만25세-29세, 만30세-34세} 로 필터링,
     * C2_NM 카테고리에 따라 DT 값을 AgeGroup(LOW/MIDDLE/HIGH)에 매핑하여 업서트한다.
     */
    @Transactional
    public void ingestLatest() {
        final String uri =
                "/openapi/Param/statisticsParameterData.do?method=getList"
                        + "&apiKey=YmMxNDIyYjNhOTY4NjFmN2IzMGQyMmYxMWU5MTgxN2E="
                        + "&itmId=T001+"
                        + "&objL1=ALL&objL2=ALL&objL3=&objL4=&objL5=&objL6=&objL7=&objL8="
                        + "&format=json&jsonVD=Y"
                        + "&prdSe=F&newEstPrdCnt=1&prdInterval=1"
                        + "&outputFields=OBJ_NM+NM+ITM_NM+UNIT_NM+"
                        + "&orgId=170&tblId=DT_170002_H002";

        // 1) String으로 수신(응답이 text/html이어도 안전)
        String body = web.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (body == null || body.isBlank()) {
            log.warn("KOSIS empty body");
            return;
        }

        ArrayNode arr;
        try {
            JsonNode root = MAPPER.readTree(body);
            if (!root.isArray()) {
                log.error("KOSIS not array: {}", root.getNodeType());
                return;
            }
            arr = (ArrayNode) root;
        } catch (Exception e) {
            log.error("KOSIS parse error", e);
            return;
        }

        // 연령그룹별 누적기
        Map<AgeGroup, Acc> accMap = new EnumMap<>(AgeGroup.class);
        accMap.put(AgeGroup.LOW, new Acc());
        accMap.put(AgeGroup.MIDDLE, new Acc());
        accMap.put(AgeGroup.HIGH, new Acc());

        int processed = 0;
        int mapped = 0;
        int skippedCat = 0;

        // 2) 필요한 필드 꺼내서 필터링/매핑
        for (JsonNode row : arr) {
            processed++;

            // 응답에 C1_NM/C2_NM/DT가 포함돼 내려온다고 가정하되, 혹시 몰라 소문자 키도 시도
            String c1 = optText(row, "C1_NM", optText(row, "c1_nm", null));
            String c2 = optText(row, "C2_NM", optText(row, "c2_nm", null));
            String dt = optText(row, "DT",    optText(row, "dt", null));

            // 우리가 원하는 연령만
            if (c1 == null || !TARGET_AGE_LABELS.contains(c1)) continue;
            if (c2 == null || dt == null) continue;

            Optional<AgeGroup> gOpt = toAgeGroup(c1);
            Optional<CatKey> catOpt = mapCategory(c2);
            Integer value = parseInt(dt);

            if (gOpt.isEmpty()) continue;
            if (catOpt.isEmpty()) { skippedCat++; continue; }

            accMap.get(gOpt.get()).add(catOpt.get(), value);
            mapped++;
        }

        // 3) 업서트
        upsertFor(AgeGroup.LOW, accMap.get(AgeGroup.LOW));
        upsertFor(AgeGroup.MIDDLE, accMap.get(AgeGroup.MIDDLE));
        upsertFor(AgeGroup.HIGH, accMap.get(AgeGroup.HIGH));

        log.info("KOSIS ingest done. processed={}, mapped={}, skippedCat={}", processed, mapped, skippedCat);
    }

    private static String optText(JsonNode node, String key, String fallback) {
        if (node == null) return fallback;
        JsonNode n = node.get(key);
        return (n == null || n.isNull()) ? fallback : n.asText();
    }

    private void upsertFor(AgeGroup group, Acc a) {
        if (group == null || a == null) return;

        HouseholdExpense e = repo.findByAgeGroup(group).orElseGet(() -> {
            HouseholdExpense ne = new HouseholdExpense();
            ne.setAgeGroup(group);
            return ne;
        });

        e.setFoodCost(a.avg(CatKey.FOOD));
        e.setRentMonthly(a.avg(CatKey.RENT_MONTHLY));
        e.setHousingManagementFee(a.avg(CatKey.HOUSING_MGMT));
        e.setEducationCost(a.avg(CatKey.EDUCATION));
        e.setTelecomCost(a.avg(CatKey.TELECOM));
        e.setTransportationCost(a.avg(CatKey.TRANSPORT));
        e.setRecreationCultureCost(a.avg(CatKey.RECREATION));

        repo.save(e);
    }
}