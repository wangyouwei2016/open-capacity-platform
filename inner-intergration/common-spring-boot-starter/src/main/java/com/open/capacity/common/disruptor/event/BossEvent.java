package com.open.capacity.common.disruptor.event;

import com.open.capacity.common.disruptor.AsyncContext;

import lombok.Data;

/**
 * @author owen
 * boss event
 */
@Data
public class BossEvent {

    private String channel;

    private BaseEvent event;

    private AsyncContext context;

    public void clear() {
        channel = null;
        event = null;
        context = null;
    }
}