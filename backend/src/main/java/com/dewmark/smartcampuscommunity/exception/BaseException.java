package com.dewmark.smartcampuscommunity.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
