package com.goorm.sslim.dto;

/**
 * 지역별 월평균 대중교통 비용 정보 DTO
 */
public record MonthlyCostByRegionDto(
        String regionCode,   // 예: SEOUL
        String regionName,   // 예: 서울
        Integer monthlyCost  // 월평균 비용(원), null 가능
) {}