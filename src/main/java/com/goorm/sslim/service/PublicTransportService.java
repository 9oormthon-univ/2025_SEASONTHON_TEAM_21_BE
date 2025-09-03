package com.goorm.sslim.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.goorm.sslim.entity.PublicTransportStat;
import com.goorm.sslim.global.response.PublicTransportStatRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PublicTransportService {

    private final PublicTransportStatRepository repo;
    private final WebClient web = WebClient.create("https://api.odcloud.kr/api/15066825/v1");

    // 한글 지역명 ↔ 코드 매핑 (예시)
    private enum Region {
        SEOUL("서울"), BUSAN("부산"), DAEGU("대구"), INCHEON("인천"), GWANGJU("광주"),
        DAEJEON("대전"), ULSAN("울산"), SEJONG("세종"), GYEONGGI("경기"), GANGWON("강원"),
        CHUNGBUK("충북"), CHUNGNAM("충남"), JEONBUK("전북"), JEONNAM("전남"),
        GYEONGBUK("경북"), GYEONGNAM("경남"), JEJU("제주");
        final String ko; Region(String ko){this.ko=ko;} String ko(){return ko;}
        static Optional<Region> fromKo(String ko){return Arrays.stream(values()).filter(r->r.ko.equals(ko)).findFirst();}
    }

    @Transactional
    public void ingestPivot() {
        JsonNode root = web.get()
                .uri(uri -> uri.path("/uddi:351668f4-a029-4006-8f77-b6c02e422165")
                        .queryParam("serviceKey", "beea3b20dcc31046c894f13d45fee96bf3b85d023319980a8878e6a47094e77d")
                        .queryParam("perPage", 1000)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        ArrayNode data = (ArrayNode) root.path("data");

        // 지역별 누적 버퍼
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
                    BigDecimal v = parseNumber(e.getValue().asText(null));
                    mapMetric(acc.get(r), metric, v);
                });
            }
        }

        // 지역별 UPSERT
        for (Acc a : acc.values()) {
            repo.upsert(
                    a.region.name(),          // region_code
                    a.region.ko(),            // region_name
                    a.weeklyUsageCnt,
                    a.monthlyCostWon,
                    a.mainModeBusPct,
                    a.mainModeMetroPct,
                    a.cardUsagePct,
                    a.infoServiceUsagePct,
                    a.accessTimeMin,
                    a.transferServiceUsagePct,
                    a.transferCount,
                    a.transferMoveTimeMin,
                    a.transferWaitTimeMin     // 입력 없으면 NULL 유지
            );
        }
    }

    // 누적 구조체
    @Getter
    private static class Acc {
        final Region region;
        BigDecimal weeklyUsageCnt, monthlyCostWon, mainModeBusPct, mainModeMetroPct,
                cardUsagePct, infoServiceUsagePct, accessTimeMin, transferServiceUsagePct,
                transferCount, transferMoveTimeMin, transferWaitTimeMin;
        Acc(Region region){ this.region = region; }
    }

    // 지표명 스위칭 (공백/언더스코어 혼용 대비)
    private void mapMetric(Acc a, String metric, BigDecimal v) {
        String m = metric.replace(" ", "_");
        switch (m) {
            case "1주간대중교통이용횟수(회)" -> a.weeklyUsageCnt = v;
            case "한달평균대중교통비용(원)"   -> a.monthlyCostWon = v;
            case "주이용교통수단_버스(%)"     -> a.mainModeBusPct = v;
            case "주이용교통수단_도시철도(%)" -> a.mainModeMetroPct = v;
            case "교통카드이용률(%)"          -> a.cardUsagePct = v;
            case "정보제공서비스이용률(%)"     -> a.infoServiceUsagePct = v;
            case "접근소요시간(분)"            -> a.accessTimeMin = v;
            case "환승서비스이용률(%)"         -> a.transferServiceUsagePct = v;
            case "환승횟수(회)"                -> a.transferCount = v;
            case "환승이동시간(분)"            -> a.transferMoveTimeMin = v;
            case "환승대기시간(분)"            -> a.transferWaitTimeMin = v;
            default -> {} // 기타는 무시
        }
    }

    private BigDecimal parseNumber(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replace(",", "").trim();
        if (cleaned.isEmpty()) return null;
        try { return new BigDecimal(cleaned); } catch (NumberFormatException ex) { return null; }
    }
}