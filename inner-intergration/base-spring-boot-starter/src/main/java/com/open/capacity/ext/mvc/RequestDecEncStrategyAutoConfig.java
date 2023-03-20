package com.open.capacity.ext.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestDecEncStrategyAutoConfig {
    @Bean
    protected RequestDecEncStrategyRegister requestDecEncStrategyRegister(){
        return new RequestDecEncStrategyRegister();
    }
}
