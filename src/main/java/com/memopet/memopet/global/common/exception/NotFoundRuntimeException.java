package com.memopet.memopet.global.common.exception;

public class NotFoundRuntimeException extends RuntimeException{

    private String message;

    public NotFoundRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
