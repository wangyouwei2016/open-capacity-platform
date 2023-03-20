package com.open.capacity.ext.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(FeignClient.class)
public class FeignUrlResetAutoConfig {
    @Bean
    public FeignUrlResetPostProcessor feignUrlResetPostProcessor(){
        return new FeignUrlResetPostProcessor();
    }
}
