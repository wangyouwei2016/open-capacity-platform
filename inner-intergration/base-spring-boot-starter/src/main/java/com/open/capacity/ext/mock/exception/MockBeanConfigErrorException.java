package com.open.capacity.ext.mock.exception;

public class MockBeanConfigErrorException extends RuntimeException {

    public MockBeanConfigErrorException() {
    }

    public MockBeanConfigErrorException(String message) {
        super(message);
    }

    public MockBeanConfigErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockBeanConfigErrorException(Throwable cause) {
        super(cause);
    }

    public MockBeanConfigErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
