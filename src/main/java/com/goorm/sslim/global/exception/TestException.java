package com.goorm.sslim.global.exception;

import com.goorm.sslim.global.code.ErrorCode;

public class TestException extends GeneralException{

    public TestException(ErrorCode code) {
        super(code);
    }
}
