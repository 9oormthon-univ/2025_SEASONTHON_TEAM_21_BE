package com.goorm.sslim.comparison.dto.response;

import com.goorm.sslim.householdExpense.entity.AgeGroup;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComparisonResponseDto {

    private AgeGroup ageGroup;
    private Long myIncomeCost;
    private Long myHouseCost;
    private Long myFoodCost;
    private Long myTransportationCost;
    private Long myEducationCost;
    private Long myTelecomCost;
    private Long myRecreationCultureCost;
    private Long avgHouseCost;
    private Long avgFoodCost;
    private Long avgTransportationCost;
    private Long avgEducationCost;
    private Long avgTelecomCost;
    private Long avgRecreationCultureCost;

}
