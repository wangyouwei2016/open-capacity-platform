package com.open.capacity.common.feign;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.ServiceNameConstants;
import com.open.capacity.common.feign.fallback.AsyncMenuFeignClientFallbackFactory;
import com.open.capacity.common.model.SysMenu;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */

@ReactiveFeignClient(name = ServiceNameConstants.USER_SERVICE,configuration = FeignExceptionConfig.class , fallbackFactory = AsyncMenuFeignClientFallbackFactory.class, decode404 = true)
public interface AsyncMenuFeignClient {
	/**
	 * 角色菜单列表
	 * @param roleCodes
	 */
	@GetMapping(value = "/menus/{roleCodes}")
	Mono<List<SysMenu>> findByRoleCodes(@RequestHeader(CommonConstant.TENANT_ID_PARAM) String tenant , @PathVariable("roleCodes") String roleCodes);
}
