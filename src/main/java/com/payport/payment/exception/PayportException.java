package com.payport.payment.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class PayportException extends RuntimeException {

    private final HttpStatus status;

    public PayportException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}