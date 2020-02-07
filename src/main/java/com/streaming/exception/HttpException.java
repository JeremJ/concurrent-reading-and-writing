package com.streaming.exception;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
