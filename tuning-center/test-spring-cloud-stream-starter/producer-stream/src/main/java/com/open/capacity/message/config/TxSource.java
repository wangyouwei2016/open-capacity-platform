package com.open.capacity.message.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TxSource {

	String OUTPUT = "tx-output";
	@Output(TxSource.OUTPUT)
	MessageChannel output();

}
