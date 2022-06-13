package com.open.capacity.common.disruptor.handler;

import java.util.List;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.WorkHandler;
import com.open.capacity.common.disruptor.WorkEventBus;
import com.open.capacity.common.disruptor.WorkEventBusManager;
import com.open.capacity.common.disruptor.event.BossEvent;
import com.open.capacity.common.disruptor.event.WorkEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
 

/**
 * @author owen
 * boss处理器
 * 多消费者不重复消费问题
 */
public class BossEventHandler implements WorkHandler<BossEvent> {

    @Override
    public void onEvent(BossEvent event) throws Exception {
        try {
            dispatchBossEvent(event);
        } finally {
            event.clear();
        }
    }

    /**
     * 只做事件分发
     */
    @SuppressWarnings("unchecked")
    private void dispatchBossEvent(BossEvent event) {
        // 1、根据channel获取到对应的workerEventBus
        WorkEventBus workEventBus = WorkEventBusManager.getSingleton().getWorkEventBus(event.getChannel());
        // 2、根据事件类型获取到对应的listener，把我们之前注册的listener拿出来
        List<EventListener> eventListeners = workEventBus.getEventListeners(event.getEvent());
        // 3、封装workEvent
        EventTranslator<WorkEvent> translator = (e, s) -> {
            e.setEvent(event.getEvent());   // 不能丢，事件类型
            e.setContext(event.getContext());  // 不能丢，数据上下文
            e.setListeners(eventListeners); // 注册到work event bus里的listener，WOrkEvent
        };
        // 4、分发到指定的workerEventBus，把你的event分发到他channel指定的work event bus里去
        // WorkEvent肯定会进入到内存队列里去，内部会有一个线程，拿到WorkEvent，会在这边交给我们的work event handler处理
        boolean publish = workEventBus.publish(translator);
        if (!publish) {
            // 如果说发布到work event bus里，队列满的问题
            // TODO: 也不一定要去做一个处理，打印一个warn log，警告你一下，页面渲染任务流转到这个地方
            // 进入event bus失败了，warn
            // 针对基于内存双总线架构，进行多步骤流转，机器重启，队列满，如何进行补偿机制+幂等机制
        }
    }
}