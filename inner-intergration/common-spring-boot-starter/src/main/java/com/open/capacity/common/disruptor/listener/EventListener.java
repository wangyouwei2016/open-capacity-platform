package com.open.capacity.common.disruptor.listener;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.handler.BossEventHandler;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author owen
 * 模仿java.util.EventListener实现观察者模型
 * 
 */
public abstract class EventListener<E extends BaseEvent>  {

	@Getter
	@Setter
	private int order;

	/**
	 * BossEventHandler在分发事件的时候, 会调用这个方法把事件传进来, 来找能处理这个事件的listener
	 * 如果接受这个事件，那就会调onEvent方法, 反之就会不调onEvent方法
	 * 找你的listener来做一个判断，accept，你是否可以接收这个event事件，如果可以接收的话，返回true
	 * 
	 * @param event 自己定义的事件
	 * @see BossEventHandler
	 */
	public abstract  boolean accept(BaseEvent event);
	

	/**
	 * 实现处理具体事件的逻辑 再把这个event交给你的onEvent来进行处理
	 * 
	 * @param event 自己定义的事件
	 */
	public abstract  void onEvent(E event, AsyncContext eventContext);
	

}