package com.open.capacity.common.disruptor.autoconfigure;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import com.open.capacity.common.disruptor.BossEventBus;
import com.open.capacity.common.disruptor.DisruptorTemplate;
import com.open.capacity.common.disruptor.WorkEventBusManager;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.ExecutorService;
import com.open.capacity.common.disruptor.util.CglibUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author owen
 */
@Configuration
@EnableConfigurationProperties({BossConfig.class, WorkerConfig.class, ExecutorConfig.class})
public class AsyncAutoConfigure implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private final BossConfig bossConfig;

    private final WorkerConfig workerConfig;

    private final ExecutorConfig executorConfig;

    private ApplicationContext applicationContext;

    public AsyncAutoConfigure(BossConfig bossConfig, WorkerConfig workerConfig, ExecutorConfig executorConfig) {
        this.bossConfig = bossConfig;
        this.workerConfig = workerConfig;
        this.executorConfig = executorConfig;
    }

    @Bean
    @Conditional(EventBusCondition.class)
    @ConditionalOnMissingBean
    public BossEventBus bossEventBus() {
        return new BossEventBus(bossConfig, workerConfig);
    }

    
    @Bean
    @ConditionalOnBean(BossEventBus.class)
    public DisruptorTemplate disruptorTemplate(BossEventBus bossEventBus) {
    	
    	return new DisruptorTemplate(bossEventBus);
    }
    
    @Bean
    @Conditional(ExecutorCondition.class)
    @ConditionalOnMissingBean
    public ExecutorService executorService() {
        return new ExecutorService(executorConfig);
    }

    // BossEventBuss+多个WorkEventBus -> 双总线架构
    // 每个WorkEventBus -> ExecutorService，线程池，一套架构，做到什么，基于内存队列进行异步任务的转发和处理
    // 如果这两个东西都搞定了以后，应用系统启动之后，他会有一个回调，他会把你的workEventBus对应的listener监听器扫描出来
    // 把listener注册到你的work event bus里去

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, EventListener> eventListenerMap = applicationContext.getBeansOfType(EventListener.class);
        WorkEventBusManager workEventBusManager = WorkEventBusManager.getSingleton();
        //排序
        Collection<EventListener> listeners = eventListenerMap.values();
        if (CollectionUtils.isNotEmpty(listeners)  ) {
        	   for (EventListener listener : listeners) {
        		   listener.setOrder(resolveOrder(listener));
               }
        }
        for (EventListener eventListener : listeners.stream().sorted(Comparator.comparing(EventListener::getOrder)).collect(Collectors.toList()) ) {
            // 这里需要过滤一下代理类，比如用了Sentinel啥的，可能就会对bean做增强，
            // 然后getClass获取到的是一个代理类，代理类上是拿不到Channel注解的
            Class<?> realClazz = CglibUtils.filterCglibProxyClass(eventListener.getClass());
            Channel channel = realClazz.getAnnotation(Channel.class);
            if (channel != null && !channel.value().isEmpty()) {
                // 获取出来对应的work event bus -> 注册监听器
                workEventBusManager.getWorkEventBus(channel.value()).register(eventListener);
            }
        }
    }
    
    /**
     * 获取拦截器优先级
     * @param interceptor
     * @return
     */
    private int resolveOrder(EventListener<?> eventListener) {
        if (!eventListener.getClass().isAnnotationPresent(Channel.class)) {
            return Channel.LOWEST_ORDER;
        } else {
            return eventListener.getClass().getAnnotation(Channel.class).order();
        }
    }

    

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}