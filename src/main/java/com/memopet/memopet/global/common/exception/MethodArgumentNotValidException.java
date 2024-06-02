package com.memopet.memopet.global.common.exception;

public class MethodArgumentNotValidException extends RuntimeException {

    private String message;

    public MethodArgumentNotValidException(String message) {
        super(message);
        this.message = message;
    }
}
