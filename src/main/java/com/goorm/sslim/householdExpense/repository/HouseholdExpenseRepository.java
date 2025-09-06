package com.goorm.sslim.householdExpense.repository;

import com.goorm.sslim.householdExpense.entity.AgeGroup;
import com.goorm.sslim.householdExpense.entity.HouseholdExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface HouseholdExpenseRepository extends JpaRepository<HouseholdExpense, Long> {

    /**
     * 특정 연령 그룹에 해당하는 데이터를 조회
     */
    Optional<HouseholdExpense> findByAgeGroup(AgeGroup ageGroup);
    /**
     * 모든 연령 그룹 데이터를 대상으로 각 항목의 평균을 구함
     */
    @Query(value = """
        SELECT
            AVG(food_cost)                AS foodCost,
            AVG(rent_monthly)             AS rentMonthly,
            AVG(housing_management_fee)   AS housingManagementFee,
            AVG(education_cost)           AS educationCost,
            AVG(telecom_cost)             AS telecomCost,
            AVG(transportation_cost)      AS transportationCost,
            AVG(recreation_culture_cost)  AS recreationCultureCost
        FROM household_expenses
        """, nativeQuery = true)
    AverageProjection getAverages();

    /**
     * 평균값 결과를 담기 위한 Projection
     */
    interface AverageProjection {
        BigDecimal getFoodCost();
        BigDecimal getRentMonthly();
        BigDecimal getHousingManagementFee();
        BigDecimal getEducationCost();
        BigDecimal getTelecomCost();
        BigDecimal getTransportationCost();
        BigDecimal getRecreationCultureCost();
    }
}