package com.open.capacity.message.service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.open.capacity.message.config.DelaySource;
import com.open.capacity.message.config.OrderSource;
import com.open.capacity.message.config.TxSource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerService {
	
	private final Source source;
	
	private final OrderSource orderSource;
	
	private final TxSource txSource;
	
	private final DelaySource delaySource;

	public boolean sendMsg(final Map msg) {

		MessageChannel messageChannel = source.output();
		return messageChannel.send(MessageBuilder.withPayload(msg)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());
	}
	
	
	public boolean sendDelayMsg(final Map data) {
		System.out.println( new Date());
		MessageChannel messageChannel = delaySource.output();
		Message msg= MessageBuilder.withPayload(data)
				.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, "6").build();  
		return messageChannel.send(msg);
	}
	
	
	public boolean sendOrderMsg(final Map msg) {

		MessageChannel messageChannel = orderSource.output();
		return messageChannel.send(MessageBuilder.withPayload(msg)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				  .setHeader(MessageConst.PROPERTY_TAGS,"orderly").build());
	}
	
	public boolean sendTransactionMessage(String version ){
		
		
		 // 发送半消息。。
		String id =UUID.randomUUID().toString();
		String transactionId = UUID.randomUUID().toString();
		
		
		
		Map message = Maps.newHashMap();
		message.put("id", id) ;
		message.put("username", "gitgeek") ;
		message.put("sex", "1") ;
		
		//发送消息
        return this.txSource.output()
        .send(
            MessageBuilder
                .withPayload(
                		message
                )
                // header也有妙用...
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                .setHeader("dataid", id)
                .setHeader("message", JSON.toJSONString(message))
                .setHeader("version", version)
                .build()
        );
	}



}