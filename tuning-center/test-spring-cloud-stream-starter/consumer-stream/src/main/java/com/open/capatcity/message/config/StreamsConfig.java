package com.open.capatcity.message.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j	
@EnableBinding({Sink.class,DelaySink.class,OrderSink.class,TxSink.class})
public class StreamsConfig {
	
//	@StreamListener(value = Processor.INPUT)
//	public void handle(String body) {
//		log.info("消息体 = {}", body);
//	}
	
	//全局异常
	@StreamListener(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
	public void handleError(ErrorMessage errorMessage) throws Exception {
		Throwable throwable = errorMessage.getPayload();
		log.error("截获异常", throwable);

		Message<?> originalMessage = errorMessage.getOriginalMessage();
		assert originalMessage != null;

		log.info("原始消息体 = {}", new String((byte[]) originalMessage.getPayload()));
	}

	 
}