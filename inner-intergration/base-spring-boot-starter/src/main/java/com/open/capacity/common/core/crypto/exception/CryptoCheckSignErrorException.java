package com.open.capacity.common.core.crypto.exception;

import com.open.capacity.common.core.exception.HltRuntimeException;

public class CryptoCheckSignErrorException extends HltRuntimeException {
    public CryptoCheckSignErrorException() {
    }

    public CryptoCheckSignErrorException(String message) {
        super(message);
    }

    public CryptoCheckSignErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoCheckSignErrorException(Throwable cause) {
        super(cause);
    }

    public CryptoCheckSignErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
