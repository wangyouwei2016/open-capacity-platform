package com.open.capacity.common.lb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.open.capacity.common.lb.config.FeignReactiveInterceptorConfig;

/**
 * 开启feign拦截器传递数据给下游服务，只包含基础数据
 *
 * @author someday
 * @date 2019/10/26
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({FeignReactiveInterceptorConfig.class})
public @interface EnableReactiveFeignInterceptor {

}