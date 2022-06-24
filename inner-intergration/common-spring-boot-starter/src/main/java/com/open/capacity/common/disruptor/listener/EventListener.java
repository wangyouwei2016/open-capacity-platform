package com.open.capacity.common.disruptor.listener;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.event.BaseEvent;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author someday
 * 模仿java.util.EventListener实现观察者模型
 * 
 */
public abstract class EventListener<E extends BaseEvent ,T extends AsyncContext>  {

	@Getter
	@Setter
	private int order;

	 
	public abstract  boolean accept(BaseEvent event);
	

	public abstract  void onEvent(E event, T eventContext);
	

}