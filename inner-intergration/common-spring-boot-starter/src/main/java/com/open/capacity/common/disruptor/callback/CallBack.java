package com.open.capacity.common.disruptor.callback;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.event.BaseEvent;

public interface CallBack {
	
	public void onError(String channel, BaseEvent event, AsyncContext context);

}
