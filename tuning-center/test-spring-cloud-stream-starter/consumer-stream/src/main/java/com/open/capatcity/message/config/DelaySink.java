package com.open.capatcity.message.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DelaySink {
	/**
	 * Input channel name.
	 */
	String INPUT = "delay-input";

	/**
	 * @return input channel.
	 */
	@Input(DelaySink.INPUT)
	SubscribableChannel input();

}
