
package com.open.capatcity.message.service;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.open.capacity.redis.test.RedisCenterApp;
import com.open.capacity.redis.util.RedisUtil;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RedisTest {

	private ConfigurableApplicationContext context;
	private RedisUtil redisUtil;
	private RedisTemplate redisTemplate;

	/**
	 * setup初始化容器的时候只执行一次
	 */
	@Setup(Level.Trial)
	public void init() {
		context = SpringApplication.run(RedisCenterApp.class);
		redisUtil = context.getBean(RedisUtil.class);
		redisTemplate = context.getBean(RedisTemplate.class);
	}

	@TearDown
	public void end() {
		context.close();
	}

	@Benchmark
	public void testRedisUtil() {
		redisUtil.set("aaaaaaaaa", "aaaaaaaa");
	}

	@Benchmark
	public void testRedisTemplate() {
		redisTemplate.opsForValue().set("bbbbbbbbb", "bbbbbbbbb");

	}

	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(RedisTest.class.getName() + ".*").warmupIterations(2)
				.measurementIterations(5).forks(1).build();
		new Runner(options).run();
	}

}
