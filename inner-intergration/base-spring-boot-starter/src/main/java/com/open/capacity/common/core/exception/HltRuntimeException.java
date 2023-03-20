package com.open.capacity.common.core.exception;

public class HltRuntimeException extends RuntimeException{
    public HltRuntimeException() {
    }

    public HltRuntimeException(String message) {
        super(message);
    }

    public HltRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HltRuntimeException(Throwable cause) {
        super(cause);
    }

    public HltRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
