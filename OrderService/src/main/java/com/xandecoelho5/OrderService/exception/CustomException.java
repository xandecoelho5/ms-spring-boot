package com.xandecoelho5.OrderService.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final String errorCode;
    private final int status;

    public CustomException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
