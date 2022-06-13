package com.open.capacity.common.disruptor;

import com.open.capacity.common.disruptor.callback.CallBack;
import com.open.capacity.common.disruptor.event.BaseEvent;

import lombok.Data;

/**
 * @author owen
 * 模仿ApplicationContext.pushEvent
 * 发布者设计模式
 */
@Data
public class DisruptorTemplate {

	
	private BossEventBus bossEventBus ;
	
	public DisruptorTemplate(BossEventBus bossEventBus) {
		super();
		this.bossEventBus = bossEventBus;
	}
	
	 
	 /**
     * 暴露发布事件的方法
     */
    public boolean publish(String channel, BaseEvent event, AsyncContext context) {
    	
        return  bossEventBus.publish(channel, event, context) ;
    	
    }

    /**
     * 暴露发布事件的方法
     */
    public boolean publish(String channel, BaseEvent event, AsyncContext context, CallBack callback) {
    	
    	 // boss event bus里封装了disruptor，内存队列
        //  尝试把我们的event发布到disruptor内存队列里面去，投递成功了以后
        //  投递失败回调方案
        boolean success =   bossEventBus.publish(channel, event, context) ;
        if (!success) {
        	callback.onError(channel, event, context);
        }
        return success;
    	
    }
	
}
