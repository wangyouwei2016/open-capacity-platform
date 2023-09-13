package com.open.capacity.common.feign.fallback;


import com.google.common.collect.ImmutableList;
import com.open.capacity.common.feign.AsyncMenuFeignClient;

import lombok.extern.slf4j.Slf4j;
import reactivefeign.FallbackFactory;
import reactor.core.publisher.Mono;

/**
 * menuService降级工场
 * 
 * @author someday
 * @date 2018/1/18
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class AsyncMenuFeignClientFallbackFactory implements FallbackFactory<AsyncMenuFeignClient> {@Override
	public AsyncMenuFeignClient apply(Throwable throwable) {
		// TODO Auto-generated method stub
		return (clientId,roleIds) -> {
			log.error("调用findByRoleCodes异常：{}", roleIds, throwable);
			return Mono.just( ImmutableList.of());
		}; 
	}

}
