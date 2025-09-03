package com.goorm.sslim.controller;

import com.goorm.sslim.global.code.ErrorCode;
import com.goorm.sslim.global.code.ResponseCode;
import com.goorm.sslim.global.exception.TestException;
import com.goorm.sslim.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping("/execute")
    public ApiResponse<Void> test(@RequestParam String error) {
        if(error.equals("yes")) {
            throw new TestException(ErrorCode._BAD_REQUEST);
        }

        return ApiResponse.onSuccess(ResponseCode.OK, null);
    }
}
