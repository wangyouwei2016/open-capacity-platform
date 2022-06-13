package com.open.capacity.common.disruptor.thread;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.open.capacity.common.disruptor.autoconfigure.ExecutorConfig;

/**
 * @author owen
 * 不同topic对应的线程池
 */
public class ExecutorService {
    private static final ConcurrentHashMap<String, SafeThreadPool> BUFFER = new ConcurrentHashMap<>();

    public ExecutorService(ExecutorConfig executorConfig) {
        for (ExecutorConfig.Config config : executorConfig.getExecutors()) {
            BUFFER.put(config.getThreadPool(), new SafeThreadPool(config.getThreadPool(), config.getThreadCount()));
        }
    }

    public void execute(String channel, Runnable task) {
        Optional.ofNullable(BUFFER.get(channel)).ifPresent(safeThreadPool -> safeThreadPool.execute(task));
    }
}