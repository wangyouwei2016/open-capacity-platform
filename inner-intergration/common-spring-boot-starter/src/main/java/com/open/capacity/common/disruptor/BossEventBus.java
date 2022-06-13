package com.open.capacity.common.disruptor;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.open.capacity.common.disruptor.autoconfigure.BossConfig;
import com.open.capacity.common.disruptor.autoconfigure.WorkerConfig;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.event.BossEvent;
import com.open.capacity.common.disruptor.handler.BossEventHandler;
import com.open.capacity.common.disruptor.thread.DaemonThreadFactory;
import com.open.capacity.common.disruptor.thread.DisruptorShutdownHook;

import lombok.Data;

/**
 * @author someday
 */
@Data
public class BossEventBus {

    private final Disruptor<BossEvent> bossRingBuffer;

    public BossEventBus(BossConfig bossConfig,
                        WorkerConfig workerConfig) {
 
        WorkEventBusManager workEventBusManager = WorkEventBusManager.getSingleton();
        for (WorkerConfig.Config config : workerConfig.getWorkers()) {
            workEventBusManager.register(config);
        }

        bossRingBuffer = new Disruptor<>(BossEvent::new, bossConfig.getRingbufferSize(),
        		DaemonThreadFactory.getInstance("BossEventBus"));
        BossEventHandler[] eventHandlers = new BossEventHandler[bossConfig.getEventHandlerNum()];
        for (int i = 0; i < eventHandlers.length; i++) {
            eventHandlers[i] = new BossEventHandler();
        }
        bossRingBuffer.handleEventsWithWorkerPool(eventHandlers);
        bossRingBuffer.start();
        
		Runtime.getRuntime().addShutdownHook(new DisruptorShutdownHook(bossRingBuffer));
        
    }

    
    public boolean publish(String channel, BaseEvent event, AsyncContext context) {
        
        EventTranslator<BossEvent> translator = (e, s) -> {
            e.setChannel(channel);
            e.setEvent(event);
            e.setContext(context);
        };
        
        return  bossRingBuffer.getRingBuffer().tryPublishEvent(translator);
    }

}