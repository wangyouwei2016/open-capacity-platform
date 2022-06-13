package com.open.capacity.common.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import com.open.capacity.common.disruptor.event.WorkEvent;
import com.open.capacity.common.disruptor.listener.EventListener;

/**
 * @author owen
 * work处理器
 * 多消费者不重复消费问题
 */
public class WorkEventHandler implements WorkHandler<WorkEvent> {

    @Override
    public void onEvent(WorkEvent event) throws Exception {
        try {
            processWorkEvent(event);
        } finally {
            event.clear();
        }
    }

    /**
     * 这里才开始处理事件
     */
    @SuppressWarnings("unchecked")
    private void processWorkEvent(WorkEvent event) {
        // 他的work event  handler，会在这里遍历你的注册所有的listener，回调你的监听器
        // work event bus里的消息，等于会交给你的listener来进行处理
    	// 顺序执行handler
        for (EventListener listener : event.getListeners()) {
            listener.onEvent(event.getEvent(), event.getContext());
        }
    }
}