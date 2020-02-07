package com.streaming.exception;

import lombok.Getter;

@Getter
public class InputException extends RuntimeException {

    public InputException(String message) {
        super(message);
    }
}
