package com.goorm.sslim.comparison.service;

import com.goorm.sslim.comparison.dto.request.ComparisonRequestDto;
import com.goorm.sslim.comparison.dto.response.ComparisonResponseDto;
import com.goorm.sslim.householdExpense.entity.AgeGroup;
import com.goorm.sslim.householdExpense.entity.HouseholdExpense;
import com.goorm.sslim.householdExpense.repository.HouseholdExpenseRepository;
import com.goorm.sslim.income.entity.Income;
import com.goorm.sslim.income.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ComparisonService {

    private final IncomeRepository incomeRepository;
    private final HouseholdExpenseRepository householdExpenseRepository;

    public ComparisonResponseDto getAvgCosts(ComparisonRequestDto dto) {

        AgeGroup myAgeGroup = getMyAgeGroup(dto.getMyAge());

        HouseholdExpense avgExpenses = householdExpenseRepository.findByAgeGroup(myAgeGroup)
                .orElseThrow(() -> new NoSuchElementException("해당 연령대의 평균 지출 데이터를 찾을 수 없습니다: " + myAgeGroup));

        int myIncome = getMyIncome(dto.getMyIncomeCost());

        double myCoefficient = calculateCoefficient(myIncome);

        long recommendedHouseCost = Math.round((avgExpenses.getRentMonthly()+ avgExpenses.getHousingManagementFee()) * myCoefficient);
        long recommendedFoodCost = Math.round(avgExpenses.getFoodCost() * myCoefficient);
        long recommendedTransportationCost = Math.round(avgExpenses.getTransportationCost() * myCoefficient);
        long recommendedEducationCost = Math.round(avgExpenses.getEducationCost() * myCoefficient);
        long recommendedTelecomCost = Math.round(avgExpenses.getTelecomCost() * myCoefficient);
        long recommendedRecreationCultureCost = Math.round(avgExpenses.getRecreationCultureCost() * myCoefficient);

        return ComparisonResponseDto.builder()
                .ageGroup(myAgeGroup)
                .myIncomeCost(dto.getMyIncomeCost()) // 소득은 사용자의 입력값을 그대로 사용
                .avgHouseCost(recommendedHouseCost)
                .avgFoodCost(recommendedFoodCost)
                .avgTransportationCost(recommendedTransportationCost)
                .avgEducationCost(recommendedEducationCost)
                .avgTelecomCost(recommendedTelecomCost)
                .avgRecreationCultureCost(recommendedRecreationCultureCost)
                .myHouseCost(dto.getMyHouseCost())
                .myFoodCost(dto.getMyFoodCost())
                .myTransportationCost(dto.getMyTransportationCost())
                .myEducationCost(dto.getMyEducationCost())
                .myTelecomCost(dto.getMyTelecomCost())
                .myRecreationCultureCost(dto.getMyRecreationCultureCost())
                .build();


    }

    public double calculateCoefficient(int myIncome) {
        Long myBoundary = incomeRepository.findBoundaryById(myIncome)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + myIncome + "인 소득 경계값을 찾을 수 없습니다."));

        Long standardBoundary = incomeRepository.findBoundaryById(3)
                .orElseThrow(() -> new NoSuchElementException("ID가 3인 기준 소득 경계값을 찾을 수 없습니다."));

        if (standardBoundary == 0) {
            throw new ArithmeticException("기준 경계값(ID: 3)이 0이어서 나눌 수 없습니다.");
        }

        return (double) myBoundary / standardBoundary;
    }

    public int getMyIncome(Long myIncomeCost) {
        List<Income> boundaries = incomeRepository.findBoundariesForBrackets(); // DB 조회는 여기서 딱 1번!

        for (Income income : boundaries) {
            if (myIncomeCost <= income.getBoundary()) {
                return income.getIncomeId().intValue();
            }
        }
        return 5;
    }

    public AgeGroup getMyAgeGroup(Integer myAge) {
        if (myAge >= 19 && myAge <= 24) {
            return AgeGroup.LOW;
        } else if (myAge >= 25 && myAge <= 29) {
            return AgeGroup.MIDDLE;
        } else {
            return AgeGroup.HIGH;
        }
    }
}
