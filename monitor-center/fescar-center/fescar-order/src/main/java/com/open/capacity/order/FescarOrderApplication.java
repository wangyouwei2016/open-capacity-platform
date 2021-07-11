package com.open.capacity.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.open.capacity.api.feign")
public class FescarOrderApplication {

    public static void main(String[] args) {
//		固定端口启动
		SpringApplication.run(FescarOrderApplication.class, args);
    }

}
