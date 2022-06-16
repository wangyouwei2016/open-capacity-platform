package com.open.capacity.disruptor.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.DisruptorTemplate;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.disruptor.context.TAsyncContext;
import com.open.capacity.disruptor.event.OrderEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Channel("step1")
public class AService extends EventListener<OrderEvent, TAsyncContext> {

	@Autowired
	private DisruptorTemplate disruptorTemplate;

	@Override
	public boolean accept(BaseEvent event) {
		return true;
	}

	public void onEvent(OrderEvent event, TAsyncContext eventContext) {

		System.out.println("step1");
		 
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		disruptorTemplate.publish("step2", event, eventContext);
	}

}
