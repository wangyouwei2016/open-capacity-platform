package com.open.capacity.message.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;  

public interface DelaySource {
    
    String OUTPUT = "delay-output";
    @Output(DelaySource.OUTPUT)
    MessageChannel output();
    
}
