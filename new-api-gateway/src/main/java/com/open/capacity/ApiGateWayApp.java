package com.open.capacity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.open.capacity.common.feign.GlobalFeignConfig;
import com.open.capacity.common.lb.annotation.EnableReactiveFeignInterceptor;
import com.open.capacity.gateway.annotation.EnableNacosDynamicRoute;

import reactivefeign.spring.config.EnableReactiveFeignClients;

/**
 * @author someday
 * @date 2019/10/5
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableNacosDynamicRoute
@EnableReactiveFeignInterceptor
@EnableReactiveFeignClients(defaultConfiguration= GlobalFeignConfig.class)
public class ApiGateWayApp  {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayApp.class, args);
    }
}