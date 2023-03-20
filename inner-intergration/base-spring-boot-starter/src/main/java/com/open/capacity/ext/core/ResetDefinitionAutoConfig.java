package com.open.capacity.ext.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResetDefinitionAutoConfig {
    @Bean
    public ResetDefinitionBeanFactoryPostProcessor resetDefinitionBeanFactoryPostProcessor(){
        return new ResetDefinitionBeanFactoryPostProcessor();
    }
}
