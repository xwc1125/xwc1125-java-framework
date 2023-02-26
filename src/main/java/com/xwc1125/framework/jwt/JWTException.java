package com.xwc1125.framework.jwt;

import lombok.Data;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-04-09 18:59
 * @Copyright Copyright@2019
 */
@Data
public class JWTException extends Throwable {
    private int code;
    private String message;

    public JWTException(JWTConstants.JWT_CODE jwt_code) {
        super(jwt_code.msg);
        this.code = jwt_code.value;
        this.message = jwt_code.msg;
    }

    public JWTException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public JWTException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public JWTException(int code, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = cause.getMessage();
    }

    public JWTException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message;
    }
}
