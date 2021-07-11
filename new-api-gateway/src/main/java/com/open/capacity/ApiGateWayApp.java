package com.open.capacity;


import com.open.capacity.common.config.TraceFilterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

 
@EnableCircuitBreaker
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TraceFilterConfig.class))
@SpringBootApplication 
public class ApiGateWayApp {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayApp.class, args);
    }
}