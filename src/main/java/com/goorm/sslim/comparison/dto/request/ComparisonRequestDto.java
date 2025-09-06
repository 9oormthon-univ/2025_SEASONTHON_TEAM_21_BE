package com.goorm.sslim.comparison.dto.request;

import com.goorm.sslim.householdExpense.entity.AgeGroup;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class ComparisonRequestDto {

    Integer myAge;
    Long myIncomeCost;
    Long myHouseCost;
    Long myFoodCost;
    Long myTransportationCost;
    Long myEducationCost;
    Long myTelecomCost;
    Long myRecreationCultureCost;
}
