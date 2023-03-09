package com.xandecoelho5.ProductService.exception;

import com.xandecoelho5.ProductService.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(ProductServiceCustomException.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(ProductServiceCustomException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }
}
