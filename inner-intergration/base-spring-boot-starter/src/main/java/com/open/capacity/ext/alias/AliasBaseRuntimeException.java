package com.open.capacity.ext.alias;

import com.open.capacity.ext.exception.HsExtRuntimeException;

public class AliasBaseRuntimeException extends HsExtRuntimeException {
    public AliasBaseRuntimeException() {
    }

    public AliasBaseRuntimeException(String message) {
        super(message);
    }

    public AliasBaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AliasBaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public AliasBaseRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
