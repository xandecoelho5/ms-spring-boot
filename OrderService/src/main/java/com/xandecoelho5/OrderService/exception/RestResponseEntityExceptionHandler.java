package com.xandecoelho5.OrderService.exception;

import com.xandecoelho5.OrderService.external.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), ex.getErrorCode()), HttpStatus.valueOf(ex.getStatus()));
    }
}
