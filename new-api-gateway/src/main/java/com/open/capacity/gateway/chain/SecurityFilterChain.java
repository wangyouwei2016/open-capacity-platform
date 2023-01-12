package com.open.capacity.gateway.chain;

import javax.annotation.Resource;

import org.apache.commons.chain.impl.ChainBase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class SecurityFilterChain extends ChainBase implements InitializingBean {

	@Resource
	private ActuatorCommand actuatorCommand;
	@Resource
	private BlackListCommand blackListCommand;
	@Resource
	private RateLimitCommand rateLimitCommand;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 将请求处理者角色加入链中
		addCommand(actuatorCommand);
		addCommand(blackListCommand);
		addCommand(rateLimitCommand);

	}

}
