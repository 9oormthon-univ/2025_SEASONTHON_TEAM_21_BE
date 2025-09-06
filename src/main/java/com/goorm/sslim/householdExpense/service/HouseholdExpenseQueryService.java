package com.goorm.sslim.householdExpense.service;

import com.goorm.sslim.householdExpense.entity.AgeGroup;
import com.goorm.sslim.householdExpense.repository.HouseholdExpenseRepository;
import com.goorm.sslim.householdExpense.repository.HouseholdExpenseRepository.AverageProjection;
import com.goorm.sslim.householdExpense.dto.HouseholdExpenseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseholdExpenseQueryService {

    private final HouseholdExpenseRepository repo;

    /**
     * 전체 연령 데이터를 대상으로 각 항목의 평균 값을 계산하여 반환
     * - ageGroup 은 응답에서 'AVG' 로 고정
     * - DB에 저장하지 않음
     */
    public HouseholdExpenseResponse getAverages() {
        AverageProjection p = repo.getAverages();

        return new HouseholdExpenseResponse(
                AgeGroup.AVG,                              // 응답 전용 가상 그룹
                toInt(p.getFoodCost()),                    // 식료품비
                toInt(p.getRentMonthly()),                 // 주거비(월세)
                toInt(p.getHousingManagementFee()),        // 주거 관리비
                toInt(p.getEducationCost()),               // 교육비
                toInt(p.getTelecomCost()),                 // 통신비
                toInt(p.getTransportationCost()),          // 교통비
                toInt(p.getRecreationCultureCost())        // 오락·문화비
        );
    }

    /**
     * (반올림)
     */
    private Integer toInt(BigDecimal v) {
        if (v == null) return null;
        // 소수점이 있을 수 있으므로 반올림해서 int 변환
        return v.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}