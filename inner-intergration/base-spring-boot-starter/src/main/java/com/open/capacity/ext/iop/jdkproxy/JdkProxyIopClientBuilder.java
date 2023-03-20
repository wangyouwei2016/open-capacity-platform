package com.open.capacity.ext.iop.jdkproxy;

import com.open.capacity.ext.iop.BaseIopClientBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public abstract class JdkProxyIopClientBuilder extends BaseIopClientBuilder implements InvocationHandler {
    @Override
    public <T> T buildClient() {
        return (T) Proxy.newProxyInstance(getInterfaceClazz().getClassLoader(),new Class[]{getInterfaceClazz()},this);
    }
}
