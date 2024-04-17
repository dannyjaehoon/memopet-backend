package com.memopet.memopet.global.common.exception;

public class BadCredentialsRuntimeException extends RuntimeException{

    private String message;

    public BadCredentialsRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
