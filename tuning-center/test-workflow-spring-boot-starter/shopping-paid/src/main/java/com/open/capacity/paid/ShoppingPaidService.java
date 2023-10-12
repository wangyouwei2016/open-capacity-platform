package com.open.capacity.paid;

import java.util.concurrent.TimeUnit;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShoppingPaidService {

    @Bean
    @ExternalTaskSubscription(topicName = "shopping_paid", processDefinitionKeyIn = {"Process_shopping"},lockDuration=500000000)
    public ExternalTaskHandler doSelfRepair() {
        return (externalTask, externalTaskService) -> {
            System.out.println("处理支付单");
            try {
                TimeUnit.SECONDS.sleep(1);
                externalTaskService.complete(externalTask);
            } catch (InterruptedException e) {
            	externalTaskService.handleFailure(externalTask, "处理支付单", "这里可以打印异常stacktrace", 1, 5000);
                throw new RuntimeException(e);
            }
            
        };


    }
}

