package com.memopet.memopet.global.common.exception;

public class ForbiddenRuntimeException extends RuntimeException{

    private String message;

    public ForbiddenRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
