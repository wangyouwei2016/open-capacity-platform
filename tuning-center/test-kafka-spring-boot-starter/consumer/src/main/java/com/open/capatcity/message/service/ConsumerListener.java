package com.open.capatcity.message.service;
 

import java.util.Map;

import com.open.capatcity.message.config.Sink;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {
	@StreamListener(target = Sink.INPUT)
    public void handleGreetings(@Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment ,@Payload Map msg) {
        System.out.println("Received greetings: " +  msg);
        acknowledgment.acknowledge();
    }
}
