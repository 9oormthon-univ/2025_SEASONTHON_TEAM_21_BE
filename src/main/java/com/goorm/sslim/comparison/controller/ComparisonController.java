package com.goorm.sslim.comparison.controller;

import com.goorm.sslim.comparison.dto.request.ComparisonRequestDto;
import com.goorm.sslim.comparison.dto.response.ComparisonResponseDto;
import com.goorm.sslim.comparison.service.ComparisonService;
import com.goorm.sslim.global.code.ResponseCode;
import com.goorm.sslim.global.response.ApiResponse;
import com.goorm.sslim.householdExpense.entity.AgeGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/comparison")
@RestController
public class ComparisonController {

    private final ComparisonService comparisonService;

    @GetMapping("/average")
    public ApiResponse<ComparisonResponseDto> getAverageExpense(
            @RequestParam ComparisonRequestDto comparisonRequestDto
            ) {

        ComparisonResponseDto result = comparisonService.getAvgCosts(comparisonRequestDto);

        return ApiResponse.onSuccess(ResponseCode.OK, result);
    }
}
