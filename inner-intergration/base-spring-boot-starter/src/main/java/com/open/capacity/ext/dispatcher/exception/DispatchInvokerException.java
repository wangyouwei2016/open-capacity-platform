package com.open.capacity.ext.dispatcher.exception;

import com.open.capacity.ext.exception.HsExtException;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-17 13:48
 */
public class DispatchInvokerException extends HsExtException {
    public DispatchInvokerException() {
    }

    public DispatchInvokerException(String message) {
        super(message);
    }

    public DispatchInvokerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatchInvokerException(Throwable cause) {
        super(cause);
    }

    public DispatchInvokerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
