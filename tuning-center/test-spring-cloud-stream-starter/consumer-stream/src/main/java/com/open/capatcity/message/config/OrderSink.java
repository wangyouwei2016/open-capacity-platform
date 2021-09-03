package com.open.capatcity.message.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OrderSink {
	/**
	 * Input channel name.
	 */
	String INPUT = "order-input";

	/**
	 * @return input channel.
	 */
	@Input(OrderSink.INPUT)
	SubscribableChannel input();

}
