package com.goorm.sslim.controller;

import com.goorm.sslim.foodcost.dto.response.ProductPriceInfoDTO;
import com.goorm.sslim.global.code.ResponseCode;
import com.goorm.sslim.global.response.ApiResponse;
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

    @GetMapping("/prices")
    public ApiResponse<Void> test(@RequestParam String goodInspectDay, String goodId) throws JAXBException {
        productPriceInfoService.calculateAndSaveAverage(goodInspectDay, goodId);

        return ApiResponse.onSuccess(ResponseCode.OK, null);
    }
}