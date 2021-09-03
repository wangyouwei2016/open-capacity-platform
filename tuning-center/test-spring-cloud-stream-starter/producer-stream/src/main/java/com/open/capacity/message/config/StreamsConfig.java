package com.open.capacity.message.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableBinding({Source.class, DelaySource.class ,OrderSource.class,TxSource.class})
public class StreamsConfig {

//	@Bean	
//    @InboundChannelAdapter(value = Processor.OUTPUT,	
//            poller = @Poller(fixedDelay = "1000", maxMessagesPerPoll = "1"))	
//	//采用@EnableBinding结合@InboundChannelAdapter注解实现消息的发送；
    public MessageSource<String> test() {	
        return () -> new GenericMessage<>("{\"sex\":\"1\",\"id\":\"9fd27dbe-e3ec-4b4e-b10b-f86dba2741b7\",\"username\":\"gitgeek\"}");	
    }	
}