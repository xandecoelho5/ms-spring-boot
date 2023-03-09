package com.xandecoelho5.ProductService.exception;

import lombok.Getter;

@Getter
public class ProductServiceCustomException extends RuntimeException {

    private final String errorCode;

    public ProductServiceCustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
