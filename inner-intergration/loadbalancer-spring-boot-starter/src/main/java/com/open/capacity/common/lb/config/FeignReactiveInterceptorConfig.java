package com.open.capacity.common.lb.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.SecurityConstants;

import cn.hutool.core.util.StrUtil;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

/**
 * feign拦截器，只包含基础数据
 *
 * @author someday
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class FeignReactiveInterceptorConfig {
    /**
     * 使用feign client访问别的微服务时，将上游传过来的client等信息放入header传递给下一个服务
     */
    @Bean
    public ReactiveHttpRequestInterceptor  baseFeignInterceptor() {
        return  new ReactiveHttpRequestInterceptor () {
			@Override
			public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest request) {
				List<String> tenants = request.headers().get(CommonConstant.TENANT_ID_PARAM);
				if (CollectionUtils.isNotEmpty(tenants)) {
	                request.headers().put(SecurityConstants.TENANT_HEADER,tenants);
	            }
				return Mono.just(request);
			}
        };
    }
}
