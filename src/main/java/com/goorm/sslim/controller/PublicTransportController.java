package com.goorm.sslim.controller;

import com.goorm.sslim.dto.MonthlyCostByRegionDto;
import com.goorm.sslim.service.PublicTransportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public-transport")
@RequiredArgsConstructor
public class PublicTransportController {
    private final PublicTransportQueryService service;

    /**
     * 전체 지역의 월평균 교통비 조회 (지역명 오름차순 정렬)
     * GET /public-transport/monthly-cost
     */
    @GetMapping("/monthly-cost")
    public List<MonthlyCostByRegionDto> getAllMonthlyCost() {
        return service.getMonthlyCost();
    }

    /**
     * 특정 지역의 월평균 교통비 조회
     * 예: /monthly-cost/region?regionName=서울
     */
    @GetMapping("/monthly-cost/region")
    public List<MonthlyCostByRegionDto> getMonthlyCostByRegion(@RequestParam String regionName) {
        return service.getByRegionName(regionName);
    }


}
