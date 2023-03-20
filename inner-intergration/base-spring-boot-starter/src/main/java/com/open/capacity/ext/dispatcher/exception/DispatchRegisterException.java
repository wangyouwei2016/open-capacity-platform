package com.open.capacity.ext.dispatcher.exception;

import com.open.capacity.ext.exception.HsExtRuntimeException;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-17 13:48
 */
public class DispatchRegisterException extends HsExtRuntimeException {
    public DispatchRegisterException() {
    }

    public DispatchRegisterException(String message) {
        super(message);
    }

    public DispatchRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatchRegisterException(Throwable cause) {
        super(cause);
    }

    public DispatchRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
