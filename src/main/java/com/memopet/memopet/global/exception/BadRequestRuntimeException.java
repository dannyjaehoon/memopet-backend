package com.memopet.memopet.global.exception;

public class BadRequestRuntimeException extends RuntimeException {

    private String message;

    public BadRequestRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
