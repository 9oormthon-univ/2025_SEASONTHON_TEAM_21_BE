package com.goorm.sslim.service;

import com.goorm.sslim.dto.ScaledHouseholdExpenseResponse;
import com.goorm.sslim.householdExpense.repository.HouseholdExpenseRepository;
import com.goorm.sslim.income.entity.Income;
import com.goorm.sslim.income.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.*;


@Service
@RequiredArgsConstructor
public class HouseholdExpenseIncomeScaleService {

    private final HouseholdExpenseRepository expenseRepo;
    private final IncomeRepository incomeRepo;

    private static final List<String> TARGET_GROUPS = List.of("1분위", "2분위", "3분위", "4분위", "5분위");

    public List<ScaledHouseholdExpenseResponse> scaleAvgByIncomeQuintile() {
        // 1) 평균(AVG) 가져오기
        var avg = expenseRepo.getAverages();
        if (avg == null) throw new IllegalStateException("household_expenses 평균 데이터를 찾을 수 없습니다.");

        // 2) 연도 결정
        String year = String.valueOf(Year.now().getValue());

        // 3) 3분위 경계(분모) 확보
        Income q3 = incomeRepo.findByGroup("3분위")
                .orElseThrow(() -> new IllegalStateException("소득 경계(3분위) 데이터가 없습니다. year=" + year));

        if (q3.getBoundary() == null || q3.getBoundary() <= 0L) {
            throw new IllegalStateException("소득 경계(3분위)가 0 이하입니다. year=" + year);
        }
        BigDecimal denom = BigDecimal.valueOf(q3.getBoundary());

        // 4) 각 분위(1~4) 경계로 비율 계산 후 평균 금액에 곱
        List<ScaledHouseholdExpenseResponse> out = new ArrayList<>();
        for (String g : TARGET_GROUPS) {
            Income qi = incomeRepo.findByGroup(g)
                    .orElseThrow(() -> new IllegalStateException("소득 경계가 없습니다. group=" + g + ", year=" + year));

            BigDecimal ratio = BigDecimal.valueOf(qi.getBoundary())
                    .divide(denom, 6, RoundingMode.HALF_UP); // 소수점 6자리

            // 곱셈 후 반올림 정수화
            ScaledHouseholdExpenseResponse dto = ScaledHouseholdExpenseResponse.builder()
                    .group(g)
                    .income(qi.getBoundary())
                    .ratio(ratio)
                    .foodCost(scale(avg.getFoodCost(), ratio))
                    .rentMonthly(scale(avg.getRentMonthly(), ratio))
                    .housingManagementFee(scale(avg.getHousingManagementFee(), ratio))
                    .educationCost(scale(avg.getEducationCost(), ratio))
                    .telecomCost(scale(avg.getTelecomCost(), ratio))
                    .transportationCost(scale(avg.getTransportationCost(), ratio))
                    .recreationCultureCost(scale(avg.getRecreationCultureCost(), ratio))
                    .build();

            out.add(dto);
        }
        return out;
    }

    private static Integer scale(java.math.BigDecimal base, BigDecimal ratio) {
        if (base == null) return null;
        // base는 AverageProjection에서 BigDecimal로 들어옴(AVG 결과)
        return base.multiply(ratio).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}