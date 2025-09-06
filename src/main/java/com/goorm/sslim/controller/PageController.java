package com.goorm.sslim.controller;

import com.goorm.sslim.dto.ScaledHouseholdExpenseResponse;
import com.goorm.sslim.service.HouseholdExpenseIncomeScaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PageController {

    private final HouseholdExpenseIncomeScaleService scaleService;

    @GetMapping("/scaled-by-income")
    public List<ScaledHouseholdExpenseResponse> getScaledByIncome() {
        return scaleService.scaleAvgByIncomeQuintile();
    }
}
