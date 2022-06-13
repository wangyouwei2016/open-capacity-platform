package com.open.capacity.common.disruptor.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author owen
 */
public class SafeThreadPool {

    private final Semaphore semaphore;

    private final ThreadPoolExecutor threadPoolExecutor;

    public SafeThreadPool(String name, int permits) {
        // 线程数量封装Semaphore信号量
        semaphore = new Semaphore(permits);

        // 拿我们的线程数量封装一个线程池，maximum最大 线程数量的大小，线程数量  * 2
        // 这个里面，提交任务，最大的话，就是会有你的max threads来跑
        // 如果线程不够了，没法进行队列的暂存
        threadPoolExecutor = new ThreadPoolExecutor(
                0,
                permits * 2,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                DaemonThreadFactory.getInstance(name)
        );
    }

    public void execute(Runnable task) {
        // 对于这个线程池，来提交 任务，每次提交，都必须在这里获取一个信号量
        // 最多同时只能有跟你的信号量=线程数量，任务提交到线程池里去

        // 超过线程数量的任务过来了以后，会在这里获取信号量这里，阻塞住
        // 立马会有一个线程拿到一个信号量
        semaphore.acquireUninterruptibly();
        // 把任务提交给线程池，任务提交超发的问题，任务数量超出了你的线程数量
        // max threads = 预期线程数量 * 2
        // 极端情况下，跟预期线程数量的任务，都在池子里跑，在同一时间点，大家都跑完了
        // 都释放了信号量，短时间，一下子所有的信号量都被释放，所有的线程还没彻底跑完，极端情况下
        // 可能有信号量 * 2个的线程数量，涌入进来，超发，提交任务
        threadPoolExecutor.submit(() -> {
            try {
                task.run(); // 并发的去运行和跑，如果跑完了以后的话，信号量去做一个释放
            } finally {
                semaphore.release();
            }
            // 这个线程可能在这个地方，他还没完全把自己释放出来
        });
    }
}