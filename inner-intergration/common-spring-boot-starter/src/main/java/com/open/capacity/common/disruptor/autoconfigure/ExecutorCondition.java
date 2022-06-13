package com.open.capacity.common.disruptor.autoconfigure;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author owen
 * 条件配置线程池
 */
public class ExecutorCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().containsProperty("disruptor.executors[0].threadPool")
                && context.getEnvironment().containsProperty("disruptor.executors[0].threadCount");
    }

}