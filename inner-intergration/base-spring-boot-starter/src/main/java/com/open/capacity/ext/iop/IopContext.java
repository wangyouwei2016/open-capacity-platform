package com.open.capacity.ext.iop;

import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;

public class IopContext extends NamedContextFactory<IopClientSpecification> {

    public IopContext(Class<?> defaultConfigType, String propertySourceName, String propertyName) {
        super(defaultConfigType, propertySourceName, propertyName);
    }

    public IopContext() {
        super(FeignClientsConfiguration.class, "feign", "feign.client.name");
    }
}
