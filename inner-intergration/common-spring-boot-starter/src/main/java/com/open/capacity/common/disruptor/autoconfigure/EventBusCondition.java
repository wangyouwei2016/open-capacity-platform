package com.open.capacity.common.disruptor.autoconfigure;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author owen
 * 条件配置boss
 */
public class EventBusCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().containsProperty("disruptor.async.boss.ringbufferSize")
                && context.getEnvironment().containsProperty("disruptor.async.boss.eventHandlerNum")
                && context.getEnvironment().containsProperty("disruptor.async.workers[0].channel")
                && context.getEnvironment().containsProperty("disruptor.async.workers[0].ringbufferSize")
                && context.getEnvironment().containsProperty("disruptor.async.workers[0].eventHandlerNum");
    }

}
