package com.goorm.sslim.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PublicTransportService {

    private final WebClient web = WebClient.create("https://api.odcloud.kr/api/15066825/v1");

    @Value("${spring.openapi.public-transport.service-key}")
    private String serviceKey;

    @PersistenceContext
    private EntityManager em;

    // 한글 지역명 <-> 코드 매핑
    private enum Region {
        SEOUL("서울"), BUSAN("부산"), DAEGU("대구"),
        INCHEON("인천"), GWANGJU("광주"), DAEJEON("대전"),
        ULSAN("울산"), SEJONG("세종"), GYEONGGI("경기"),
        GANGWON("강원"), CHUNGBUK("충북"), CHUNGNAM("충남"),
        JEONBUK("전북"), JEONNAM("전남"), GYEONGBUK("경북"),
        GYEONGNAM("경남"), JEJU("제주");
        final String ko; Region(String ko){this.ko=ko;} String ko(){return ko;}
        static Optional<Region> fromKo(String ko){
            return Arrays.stream(values()).filter(r->r.ko.equals(ko)).findFirst();
        }
    }

    @Transactional
    public void ingestPivot() {
        JsonNode root = web.get()
                .uri(u -> u.path("/uddi:351668f4-a029-4006-8f77-b6c02e422165")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("perPage", 1000)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        ArrayNode data = (ArrayNode) root.path("data");

        // 지역별 누적(필요 컬럼만)
        Map<Region, Acc> acc = new EnumMap<>(Region.class);
        for (Region r : Region.values()) acc.put(r, new Acc(r));

        for (JsonNode row : data) {
            String metric = row.path("구분1").asText();
            Iterator<Map.Entry<String, JsonNode>> it = row.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                String key = e.getKey();
                if ("구분1".equals(key)) continue;

                Region.fromKo(key).ifPresent(r -> {
                    String raw = e.getValue().asText(null);
                    mapMetric(acc.get(r), metric, raw);
                });
            }
        }

        upsertBatchMinimal(acc.values());
    }

    // 필요한 지표만 매핑
    private void mapMetric(Acc a, String metric, String raw) {
        String m = metric.replace(" ", "_");
        switch (m) {
            case "1주간대중교통이용횟수(회)" -> a.weeklyUsageCnt = parseDecimal(raw);
            case "한달평균대중교통비용(원)"   -> a.monthlyCost   = parseInt(raw);
            default -> {}
        }
    }

    private void upsertBatchMinimal(Collection<Acc> values) {
        final String sql = """
            INSERT INTO public_transport_stats
              (region_code, region_name, weekly_usage_cnt, monthly_cost)
            VALUES
              (:regionCode, :regionName, :weeklyUsageCnt, :monthlyCost)
            ON DUPLICATE KEY UPDATE
              region_name = VALUES(region_name),
              weekly_usage_cnt = VALUES(weekly_usage_cnt),
              monthly_cost = VALUES(monthly_cost)
            """;

        int i = 0;
        for (Acc a : values) {
            if (a.weeklyUsageCnt == null && a.monthlyCost == null) continue;

            em.createNativeQuery(sql)
                    .setParameter("regionCode", a.region.name())
                    .setParameter("regionName", a.region.ko())
                    .setParameter("weeklyUsageCnt", a.weeklyUsageCnt)
                    .setParameter("monthlyCost", a.monthlyCost)
                    .executeUpdate();

            if (++i % 50 == 0) { // 배치 플러시/클리어로 메모리 관리
                em.flush();
                em.clear();
            }
        }
    }

    @Getter
    private static class Acc {
        final Region region;
        BigDecimal weeklyUsageCnt;
        Integer    monthlyCost;
        Acc(Region region){ this.region = region; }
    }

    private BigDecimal parseDecimal(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replace(",", "").trim();
        if (cleaned.isEmpty()) return null;
        try { return new BigDecimal(cleaned); } catch (NumberFormatException ex) { return null; }
    }

    private Integer parseInt(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replace(",", "").trim();
        if (cleaned.isEmpty()) return null;
        try { return Integer.valueOf(cleaned); } catch (NumberFormatException ex) { return null; }
    }
}