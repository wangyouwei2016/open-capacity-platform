package com.open.capacity.ext.core;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public interface BeanDefinitionPostProcessor {
    boolean supportResetDefinition(BeanDefinition beanDefinition);
    void resetDefinition(String beanName, BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory);
}
