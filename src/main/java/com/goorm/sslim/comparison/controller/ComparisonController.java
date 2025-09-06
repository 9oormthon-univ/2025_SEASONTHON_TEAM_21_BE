package com.goorm.sslim.comparison.controller;

import com.goorm.sslim.comparison.dto.request.ComparisonRequestDto;
import com.goorm.sslim.comparison.dto.response.ComparisonResponseDto;
import com.goorm.sslim.comparison.service.ComparisonService;
import com.goorm.sslim.global.code.ResponseCode;
import com.goorm.sslim.global.response.ApiResponse;
import com.goorm.sslim.householdExpense.entity.AgeGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/comparison")
@RestController
public class ComparisonController {

    private final ComparisonService comparisonService;

    @PostMapping("/average")
    public ApiResponse<ComparisonResponseDto> getAverageExpense(
            @RequestBody ComparisonRequestDto comparisonRequestDto
    ) {
        ComparisonResponseDto result = comparisonService.getAvgCosts(comparisonRequestDto);

        return ApiResponse.onSuccess(ResponseCode.OK, result);
    }
}
