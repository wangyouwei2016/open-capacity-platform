package com.open.capacity.ext.iop;

import org.springframework.cloud.context.named.NamedContextFactory;

public class IopClientSpecification implements NamedContextFactory.Specification{
    private String name;

    private Class<?>[] configuration;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }

    public IopClientSpecification() {
    }

    public IopClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }
}
