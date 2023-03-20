package com.open.capacity.ext.iop;

public abstract class BaseIopClientBuilder implements IopClientBuilder{
    private Class interfaceClazz;

    @Override
    public <T> Class<T> getInterfaceClazz() {
        return interfaceClazz;
    }

    @Override
    public <T> void setInterfaceClazz(Class<T> interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }
}
