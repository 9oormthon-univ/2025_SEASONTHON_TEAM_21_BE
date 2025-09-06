package com.goorm.sslim.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //Common
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON_400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON_401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "권한이 없습니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "대상을 찾을 수 없습니다."),

    //API
    _NULL_API(HttpStatus.NOT_FOUND, "API_404", "API 응답이 null입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}