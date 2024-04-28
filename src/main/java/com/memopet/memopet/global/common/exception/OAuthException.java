package com.memopet.memopet.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuthException extends RuntimeException {
    private final String message;
}
