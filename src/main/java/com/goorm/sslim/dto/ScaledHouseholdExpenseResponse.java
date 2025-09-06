package com.goorm.sslim.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScaledHouseholdExpenseResponse {
    String group;                 // "1분위" ~ "4분위"
    BigDecimal ratio;             // (해당분위 경계 / 3분위 경계), 소수 유지

    Integer foodCost;
    Integer rentMonthly;
    Integer housingManagementFee;
    Integer educationCost;
    Integer telecomCost;
    Integer transportationCost;
    Integer recreationCultureCost;
}