package com.open.capacity.common.disruptor;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.SleepingWaitStrategy;

import com.open.capacity.common.disruptor.consumer.Consumer;

 
@Configuration
@ConditionalOnProperty(name = {"spring.disruptor.enable"}, matchIfMissing = false, havingValue = "true")
public class WorkPoolConfig {

	@PostConstruct
	public void init() {
		Consumer[] consumers = new Consumer[4];
		for (int i = 0; i < consumers.length; i++) {
			consumers[i] = new Consumer(String.format("comsumer%d", i));
		}
		RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI, 1024 * 1024,
				new SleepingWaitStrategy(), consumers);

	}

}
