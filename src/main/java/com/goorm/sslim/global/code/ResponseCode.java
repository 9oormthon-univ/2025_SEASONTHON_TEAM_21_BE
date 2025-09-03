package com.goorm.sslim.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    //Common
    OK(HttpStatus.OK, "COMMON_200", "성공입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
