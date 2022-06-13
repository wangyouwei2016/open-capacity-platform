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
 * @author owen
 */
@Data
public class BossEventBus {

    private final Disruptor<BossEvent> bossRingBuffer;

    public BossEventBus(BossConfig bossConfig,
                        WorkerConfig workerConfig) {

        // boss事件总线，主事件总线，只有一个
        // work事件总线，工作任务事件总线，有多个，不同类型

        // 所有的event先进boss event bus -> handler dispatch -> 分发到各自对应的work event bus
        // work event bus里的线程把event拿出来交给我们自定义的额work event handler去做一个处理

        // 1、先把WorkEventBus准备好
        WorkEventBusManager workEventBusManager = WorkEventBusManager.getSingleton();
        for (WorkerConfig.Config config : workerConfig.getWorkers()) {
            workEventBusManager.register(config);
        }

        // 双总线架构设计，boss event bus -> 分发到不同的额work event bus -> 不同的线程池来进行并发处理

        // 2、再把BossEventBus准备好
        // 都可以来进行分析，disruptor结构，内存队列ring buffer，我们可以把他作为一个event bus
        // 把一个一个的event bus写入到ring buffer里，disruptor
        // 必须有人来进行处理，disruptor内部肯定会有一个对应的线程，应该是可以取出来一个一个event
        // 他要把event交给我们event handler pool，多个handler来进行处理，可以只有一个event handler就可以了
        bossRingBuffer = new Disruptor<>(BossEvent::new, bossConfig.getRingbufferSize(),
        		DaemonThreadFactory.getInstance("BossEventBus"));
        //消费者
        BossEventHandler[] eventHandlers = new BossEventHandler[bossConfig.getEventHandlerNum()];
        for (int i = 0; i < eventHandlers.length; i++) {
            eventHandlers[i] = new BossEventHandler();
        }
        bossRingBuffer.handleEventsWithWorkerPool(eventHandlers);
        bossRingBuffer.start();
        
        
        /**
		 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己
		 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
		 */
		Runtime.getRuntime().addShutdownHook(new DisruptorShutdownHook(bossRingBuffer));
        
    }

    /**
     * 暴露发布事件的方法
     */
    public boolean publish(String channel, BaseEvent event, AsyncContext context) {
        // 大致认为说EventTranslator，他是把我们的数据模型，转换为disruptor里面的event类型对象
        // channel -> work event bus，每个work事件总线都是一个channel管道概念
        // 本次发布出来的是什么样的类型的event事件
        // event事件关联的context数据对象，封装了你对应的数据
        EventTranslator<BossEvent> translator = (e, s) -> {
            e.setChannel(channel);
            e.setEvent(event);
            e.setContext(context);
        };
        //通过EventTranslator来发布事件，让消费者来消费
        return  bossRingBuffer.getRingBuffer().tryPublishEvent(translator);
    }

}