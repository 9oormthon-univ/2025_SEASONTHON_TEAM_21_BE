package com.goorm.sslim.controller;

import com.goorm.sslim.foodcost.dto.response.ProductInfoDTO;
import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoDTO;
import com.goorm.sslim.global.code.ResponseCode;
import com.goorm.sslim.global.response.ApiResponse;
import com.goorm.sslim.service.ProductInfoService;
import com.goorm.sslim.service.ProductPriceInfoService;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final ProductPriceInfoService productPriceInfoService;
    private final ProductInfoService productInfoService;

    @GetMapping("/prices")
    public ApiResponse<Long> test(@RequestParam String goodInspectDay, String goodId) throws JAXBException {
        productPriceInfoService.saveAveragePrice(goodInspectDay, goodId);
        long expenditure = productPriceInfoService.getMonthlyFoodExpenditure();

        return ApiResponse.onSuccess(ResponseCode.OK, expenditure);
    }
}