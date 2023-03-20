package com.open.capacity.ext.dispatcher.exception;

import com.open.capacity.ext.exception.HsExtRuntimeException;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-17 13:48
 */
public class DispatchRouteNoFoundException extends HsExtRuntimeException {
    public DispatchRouteNoFoundException() {
    }

    public DispatchRouteNoFoundException(String message) {
        super(message);
    }

    public DispatchRouteNoFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatchRouteNoFoundException(Throwable cause) {
        super(cause);
    }

    public DispatchRouteNoFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
