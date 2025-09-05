package com.goorm.sslim.region.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.sslim.region.dto.RegionDto;
import com.goorm.sslim.region.entity.Region;
import com.goorm.sslim.region.repository.RegionRepository;

import jakarta.transaction.Transactional;
import reactor.util.retry.Retry;

@Service
public class RegionService {

	private final WebClient regionalCodeWebClient;
	private final RegionRepository regionRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Value("${apis.rtms.service-key}")
    private String serviceKey;
	
	public RegionService(
	        @Qualifier("regionalCodeWebClient") WebClient regionalCodeWebClient,
	        RegionRepository regionRepository
	    ) {
	        this.regionalCodeWebClient = regionalCodeWebClient;
	        this.regionRepository = regionRepository;
	    }
	
	private static final int PAGE_SIZE = 100;
	
	/**
     * 전국 모든 페이지를 순회하여 sgg_cd(lawdCd) 단위로 집계 후 일괄 upsert
     * - lawdCd = sgg_cd(5자리)
     * - siName = ctpv_nm
     * - sggName = sgg_nm
     */
	@Transactional
    public void syncAllLawdCdNationwide() {

        Map<String, RegionTriple> merged = new LinkedHashMap<>();
        int page = 1;

        while (true) {
            JsonNode root = callApi(page, PAGE_SIZE);
            List<RegionDto> items = extractItems(root);

            if (items.isEmpty()) break;

            Map<String, RegionTriple> pageMap = items.stream()
                    .filter(it -> it.getSgg_cd() != null && it.getSgg_cd().length() >= 5)
                    .collect(Collectors.toMap(
                            it -> it.getSgg_cd().substring(0, 5),
                            it -> new RegionTriple(
                                    it.getSgg_cd().substring(0, 5),
                                    nz(it.getCtpv_nm()),
                                    nz(it.getSgg_nm())),
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

            pageMap.forEach(merged::putIfAbsent);

            if (isLastPage(root, page, PAGE_SIZE)) break;
            page++;
        }

        if (merged.isEmpty()) return;

        Map<String, Region> existing =
                regionRepository.findAll().stream()
                        .collect(Collectors.toMap(Region::getLawdCd, r -> r));

        List<Region> toInsert = new ArrayList<>();
        List<Region> toUpdate = new ArrayList<>();

        for (RegionTriple t : merged.values()) {
            Region exist = existing.get(t.lawdCd);
            if (exist == null) {
                Region r = new Region();
                r.setLawdCd(t.lawdCd);
                r.setSiName(t.siName);
                r.setSggName(t.sggName);
                toInsert.add(r);
            } else {
                exist.setSiName(t.siName);
                exist.setSggName(t.sggName);
                toUpdate.add(exist);
            }
        }

        if (!toInsert.isEmpty()) regionRepository.saveAll(toInsert);
        if (!toUpdate.isEmpty()) regionRepository.saveAll(toUpdate);
    }
	
	/** API 호출 → JsonNode 수신 */
    private JsonNode callApi(int pageNo, int numOfRows) {
        return regionalCodeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getRegionalCode")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("dataType", "JSON")
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", numOfRows)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(ex -> ex instanceof WebClientResponseException.TooManyRequests
                                || ex instanceof WebClientResponseException.InternalServerError))
                .block();
    }
    
    /** JsonNode → List<RegionItemDto> */
    private List<RegionDto> extractItems(JsonNode root) {
        if (root == null) return List.of();
        JsonNode itemNode = root.path("Response").path("body").path("items").path("item");
        if (itemNode.isMissingNode() || itemNode.isNull()) return List.of();

        List<RegionDto> result = new ArrayList<>();
        if (itemNode.isArray()) {
            for (JsonNode node : itemNode) {
                result.add(objectMapper.convertValue(node, RegionDto.class));
            }
        } else {
            result.add(objectMapper.convertValue(itemNode, RegionDto.class));
        }
        return result;
    }
	
    /** totalCount/numOfRows 기반 마지막 페이지 판정 */
    private static boolean isLastPage(JsonNode root, int pageNo, int pageSize) {
    	
        if (root == null) return true;
        JsonNode body = root.path("Response").path("body");
        int total = asInt(body.path("totalCount"), 0);
        int rows = asInt(body.path("numOfRows"), pageSize);
        if (rows <= 0) rows = pageSize;
        
        return pageNo * rows >= total;
        
    }
    
    private static int asInt(JsonNode n, int def) {
    	
        if (n == null || n.isMissingNode() || n.isNull()) return def;
        if (n.isNumber()) return n.asInt();
        if (n.isTextual()) {
            try { return Integer.parseInt(n.asText()); }
            catch (NumberFormatException e) { return def; }
        }
        return def;
    }
    
    private static String nz(String s) { return s == null ? "" : s; }

    private record RegionTriple(String lawdCd, String siName, String sggName) {}
    
}
