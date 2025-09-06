package com.goorm.sslim.service;

import com.goorm.sslim.dto.MonthlyCostByRegionDto;
import com.goorm.sslim.entity.PublicTransportStat;
import com.goorm.sslim.repository.PublicTransportStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicTransportQueryService {

    private final PublicTransportStatRepository repo;

    @Transactional(readOnly = true)
    public List<MonthlyCostByRegionDto> getMonthlyCost() {
        List<PublicTransportStat> rows = repo.findAllWithMonthlyCostOrderByNameAsc();

        return rows.stream()
                .map(p -> new MonthlyCostByRegionDto(
                        p.getRegionCode(),
                        p.getRegionName(),
                        p.getMonthlyCost()
                ))
                .toList();
    }

    /**
     * 특정 지역명으로 조회
     * @param regionName 지역 한글명 (예: "서울", "부산")
     */
    @Transactional(readOnly = true)
    public List<MonthlyCostByRegionDto> getByRegionName(String regionName) {
        List<PublicTransportStat> rows = repo.findByRegionName(regionName);

        return rows.stream()
                .map(p -> new MonthlyCostByRegionDto(
                        p.getRegionCode(),
                        p.getRegionName(),
                        p.getMonthlyCost()
                ))
                .toList();
    }


}