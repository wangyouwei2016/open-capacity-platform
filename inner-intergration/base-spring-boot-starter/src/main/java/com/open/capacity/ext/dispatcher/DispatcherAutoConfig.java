package com.open.capacity.ext.dispatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatcherAutoConfig {
    @Bean
    protected DestinationBeanPostProcessor destinationBeanPostProcessor(){
        return new DestinationBeanPostProcessor();
    }
}
