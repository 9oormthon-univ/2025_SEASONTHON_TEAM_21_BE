package com.goorm.sslim.global.exception;

import com.goorm.sslim.global.code.ErrorCode;
import com.goorm.sslim.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 직접 정의한 비즈니스 로직 상의 예외를 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Object> response = ApiResponse.onFailure(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    // @Valid나 @Validation에서 유효성 검증 실패했을 때
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode errorCode = ErrorCode._BAD_REQUEST;
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(
                ApiResponse.onFailure(errorCode, message, null),
                errorCode.getHttpStatus()
        );
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorCode errorCode = ErrorCode._BAD_REQUEST;
        String message = String.format("'%s' 파라미터의 타입이 잘못되었습니다. 필요한 타입: '%s'",
                e.getPropertyName(),
                e.getRequiredType().getSimpleName());

        return new ResponseEntity<>(
                ApiResponse.onFailure(errorCode, message, null),
                errorCode.getHttpStatus()
        );
    }

    // 지정되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception e) {
        e.printStackTrace();
        ErrorCode errorCode = ErrorCode._INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(
                ApiResponse.onFailure(errorCode),
                errorCode.getHttpStatus()
        );
    }
}

