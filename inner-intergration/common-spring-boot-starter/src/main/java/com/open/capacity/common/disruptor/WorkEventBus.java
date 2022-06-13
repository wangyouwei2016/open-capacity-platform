package com.open.capacity.common.disruptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.DaemonThreadFactory;

/**
 * @author owen
 */
public class WorkEventBus<E> {
    private final Disruptor<E> workRingBuffer;
    // 用来存放这个workEventbus的所有的listener, 这些listener还可以按照处理的事件进一步划分
    private final List<EventListener> eventListeners = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    public WorkEventBus(int ringBufferSize,
                        int workerHandlerNum,
                        EventFactory<E> eventFactory,
                        Supplier<WorkHandler<E>> workHandlerSupplier) {

        // ring buffer size，会基于 他封装创建一disruptor，大家可以理解为，开源框架，高性能的内存队列
        workRingBuffer = new Disruptor<>(
                eventFactory,
                ringBufferSize, // 4096
                DaemonThreadFactory.getInstance("WorkEventBus") ,
                ProducerType.MULTI,
                new YieldingWaitStrategy()
        );

        WorkHandler<E>[] workHandlers = new WorkHandler[workerHandlerNum]; // 1
        for (int i = 0; i < workHandlers.length; i++) {
            workHandlers[i] = workHandlerSupplier.get();
        }

        workRingBuffer.handleEventsWithWorkerPool(workHandlers);
        workRingBuffer.start();
        
    }

    /**
     * 留给用的注册listener的方法，可以在容器生命周期的某个阶段自行手动注册listener
     */
    public boolean register(EventListener eventListener) {
        // 针对我们的监听器注册，写锁
        lock.writeLock().lock();
        try {
            if (eventListeners.contains(eventListener)) {
                return false;
            }
            eventListeners.add(eventListener);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<EventListener> getEventListeners(BaseEvent event) {
        // 如果说获取event listener，读锁，并发的去跑不同的线程
        lock.readLock().lock();
        try {
            // 把你注册过的listener都查出来
            return eventListeners.stream()
                    .filter(e -> e.accept(event))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 暴露发布事件的方法
     */
    public boolean publish(EventTranslator<E> translator) {
        return workRingBuffer.getRingBuffer().tryPublishEvent(translator);
    }
}