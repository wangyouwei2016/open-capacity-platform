package com.open.capatcity.message.service;
 

import java.util.Date;
import java.util.Map;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.open.capatcity.message.config.DelaySink;
import com.open.capatcity.message.config.OrderSink;
import com.open.capatcity.message.config.TxSink;

@Component
public class ConsumerListener {
	
	@StreamListener(target = Sink.INPUT )
    public void handleGreetings(@Payload Map msg) {
        System.out.println("Received handleGreetings: " +  msg);
    }
	
	@StreamListener(target = DelaySink.INPUT )
    public void handleDelay(@Payload Map msg) {
		System.out.println( new Date());
        System.out.println("Received handleDelay: " +  msg);
    }
	
	@StreamListener(target = OrderSink.INPUT )
    public void handleOrder(@Payload Map msg) {
        System.out.println("Received handleOrder: " +  msg);
    }
	
	@StreamListener(target = TxSink.INPUT,condition = "headers['version']=='1'" )
    public void handleVersion1(@Payload Map msg) {
        System.out.println("Received handleVersion1: " +  msg);
    }
	
	@StreamListener(target = TxSink.INPUT,condition = "headers['version']=='2'" )
    public void handleVersion2(@Payload Map msg) {
        System.out.println("Received handleVersion2: " +  msg);
    }
}
