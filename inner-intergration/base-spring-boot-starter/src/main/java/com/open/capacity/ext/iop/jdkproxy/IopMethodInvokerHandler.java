package com.open.capacity.ext.iop.jdkproxy;

import java.lang.reflect.Method;

public interface IopMethodInvokerHandler {
    Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable;
}
