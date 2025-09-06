package com.goorm.sslim.dto;

import com.goorm.sslim.entity.AgeGroup;
import com.goorm.sslim.entity.HouseholdExpense;
import lombok.Value;

@Value
public class HouseholdExpenseResponse {
    AgeGroup ageGroup;
    Integer foodCost;
    Integer rentMonthly;
    Integer housingManagementFee;
    Integer educationCost;
    Integer telecomCost;
    Integer transportationCost;
    Integer recreationCultureCost;

    public static HouseholdExpenseResponse from(HouseholdExpense e) {
        return new HouseholdExpenseResponse(
                e.getAgeGroup(),
                e.getFoodCost(),
                e.getRentMonthly(),
                e.getHousingManagementFee(),
                e.getEducationCost(),
                e.getTelecomCost(),
                e.getTransportationCost(),
                e.getRecreationCultureCost()
        );
    }
}