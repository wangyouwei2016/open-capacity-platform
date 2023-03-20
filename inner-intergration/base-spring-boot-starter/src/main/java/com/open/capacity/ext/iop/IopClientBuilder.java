package com.open.capacity.ext.iop;

import org.springframework.beans.factory.FactoryBean;

public interface IopClientBuilder extends FactoryBean<Object> {
    @Override
    default Object getObject() throws Exception {
        return buildClient();
    }
    @Override
    default Class<?> getObjectType() {
        return getInterfaceClazz();
    }
    <T> T buildClient();
    <T> Class<T> getInterfaceClazz();
    <T> void setInterfaceClazz(Class<T> interfaceClazz);

    default String contextId(){
        return "#iop." + getInterfaceClazz().getSimpleName();
    }
}
