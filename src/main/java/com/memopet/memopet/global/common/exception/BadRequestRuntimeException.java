package com.memopet.memopet.global.common.exception;

public class BadRequestRuntimeException  extends RuntimeException{

    private String message;

    public BadRequestRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
