package com.open.capacity.common.feign.fallback;

import com.open.capacity.common.feign.AsyncUserFeignClient;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysUser;

import lombok.extern.slf4j.Slf4j;
import reactivefeign.FallbackFactory;
import reactor.core.publisher.Mono;

/**
 * UserFeignClient降级工场
 *
 * @author someday
 * @date 2018/1/18 blog: https://blog.51cto.com/13005375 code:
 *       https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class AsyncUserFeignClientFallbackFactory implements FallbackFactory<AsyncUserFeignClient> {
	@Override
	public AsyncUserFeignClient apply(Throwable throwable) {
		// TODO Auto-generated method stub
		return new AsyncUserFeignClient() {

			@Override
			public Mono<SysUser> selectByUsername(String username) {
				log.error("通过用户名查询用户异常:{}", username, throwable);
				return Mono.just(new SysUser());
			}

			@Override
			public Mono<LoginAppUser> findByUsername(String username) {
				log.error("通过用户名查询用户异常:{}", username, throwable);
				return Mono.just(new LoginAppUser());
			}

			@Override
			public Mono<LoginAppUser> findByMobile(String mobile) {
				log.error("通过手机号查询用户异常:{}", mobile, throwable);
				return Mono.just(new LoginAppUser());
			}

			@Override
			public Mono<LoginAppUser> findByUserId(String userId) {
				log.error("通过userId查询用户异常:{}", userId, throwable);
				return Mono.just(new LoginAppUser());
			}

			@Override
			public Mono<LoginAppUser> findByOpenId(String openId) {
				log.error("通过openId查询用户异常:{}", openId, throwable);
				return Mono.just(new LoginAppUser());
			}

		};
	}

}
