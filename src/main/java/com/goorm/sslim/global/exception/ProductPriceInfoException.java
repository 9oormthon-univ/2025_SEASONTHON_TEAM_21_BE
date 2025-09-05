package com.goorm.sslim.global.exception;

import com.goorm.sslim.global.code.ErrorCode;

public class ProductPriceInfoException extends GeneralException{

    public ProductPriceInfoException(ErrorCode code) {
        super(code);
    }
}
