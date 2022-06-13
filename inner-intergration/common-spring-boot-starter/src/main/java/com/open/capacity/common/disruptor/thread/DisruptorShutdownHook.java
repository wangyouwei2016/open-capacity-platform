package com.open.capacity.common.disruptor.thread;

import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorShutdownHook extends Thread{
	
	private Disruptor<?> disruptor;
	
	public DisruptorShutdownHook(Disruptor<?> disruptor) {
		this.disruptor = disruptor;
	}
	
	@Override
	public void run() {
		disruptor.shutdown();
		System.out.println("shut down");
	}
	
}
