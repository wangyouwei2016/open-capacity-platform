package com.open.capatcity.message.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TxSink {
	/**
	 * Input channel name.
	 */
	String INPUT = "tx-input";

	/**
	 * @return input channel.
	 */
	@Input(TxSink.INPUT)
	SubscribableChannel input();

}
