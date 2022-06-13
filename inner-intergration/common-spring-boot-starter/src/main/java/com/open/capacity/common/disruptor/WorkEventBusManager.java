package com.open.capacity.common.disruptor;

import java.util.concurrent.ConcurrentHashMap;

import com.open.capacity.common.disruptor.autoconfigure.WorkerConfig;
import com.open.capacity.common.disruptor.event.WorkEvent;
import com.open.capacity.common.disruptor.handler.WorkEventHandler;

/**
 * @author owen
 */
public class WorkEventBusManager {

    private static final WorkEventBusManager SINGLETON = new WorkEventBusManager();

    private final ConcurrentHashMap<String, WorkEventBus> BUFFER = new ConcurrentHashMap<>();

    private WorkEventBusManager() {}

    public static WorkEventBusManager getSingleton() {
        return SINGLETON;
    }

    /**
     * 总的入口放到了bossEventBus的构造方法中，
     * 在那里先创建好所有的workerEventBus然后再创建bossEventBus
     */
    public void register(WorkerConfig.Config config) {
        BUFFER.computeIfAbsent(config.getChannel(), k -> new WorkEventBus<>(
                config.getRingbufferSize(), // ring buffer，内存队列数据结构
                config.getEventHandlerNum(), // work event bus -> 肯定是往这个里面发布event，ring buffer，event handler
                WorkEvent::new,
                WorkEventHandler::new)
        );
    }

    public WorkEventBus getWorkEventBus(String channel) {
        return BUFFER.get(channel);
    }

}