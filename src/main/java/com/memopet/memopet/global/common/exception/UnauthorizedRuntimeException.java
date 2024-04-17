package com.memopet.memopet.global.common.exception;

public class UnauthorizedRuntimeException extends RuntimeException{

    private String message;

    public UnauthorizedRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
