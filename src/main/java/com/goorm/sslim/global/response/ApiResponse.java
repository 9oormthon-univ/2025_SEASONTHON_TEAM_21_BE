package com.goorm.sslim.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.goorm.sslim.global.code.ErrorCode;
import com.goorm.sslim.global.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    private final boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public static <T> ApiResponse<T> onSuccess(ResponseCode code, T result) {
        return new ApiResponse<>(
                true,
                code.getCode(),
                code.getMessage(),
                result
        );
    }

    public static <T> ApiResponse<T> onFailure(ErrorCode code, T result) {
        return new ApiResponse<>(
                false,
                code.getCode(),
                code.getMessage(),
                result
        );
    }

    public static <T> ApiResponse<T> onFailure(ErrorCode code) {
        return new ApiResponse<>(
                false,
                code.getCode(),
                code.getMessage(),
                null
        );
    }

    // 동적으로 생성한 상세 메시지 사용
    public static <T> ApiResponse<T> onFailure(ErrorCode code, String message, T result) {
        return new ApiResponse<>(
                false,
                code.getCode(),
                message,
                result
        );
    }
}