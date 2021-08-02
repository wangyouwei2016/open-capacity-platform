package com.open.capacity.message.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;  

public interface OrderSource {
    
    String OUTPUT = "order-output";
    @Output(OrderSource.OUTPUT)
    MessageChannel output();
    
}
