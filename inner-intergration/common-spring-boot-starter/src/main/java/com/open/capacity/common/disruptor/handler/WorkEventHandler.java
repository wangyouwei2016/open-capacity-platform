package com.open.capacity.common.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import com.open.capacity.common.disruptor.event.WorkEvent;
import com.open.capacity.common.disruptor.listener.EventListener;

/**
 * @author someday
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

    
    @SuppressWarnings("unchecked")
    private void processWorkEvent(WorkEvent event) {
        for (EventListener listener : event.getListeners()) {
            listener.onEvent(event.getEvent(), event.getContext());
        }
    }
}