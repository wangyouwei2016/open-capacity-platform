package com.open.capacity.gateway.auth;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.feign.AsyncMenuFeignClient;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.uaa.common.service.impl.CustomerAccessServiceImpl;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * url权限认证
 * @author zlt
 * @date 2018/10/6
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class CustomerAccessManager extends CustomerAccessServiceImpl implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Resource
    private AsyncMenuFeignClient asyncMenuFeignClient;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.flatMap(auth -> {
            ServerWebExchange exchange = authorizationContext.getExchange();
            ServerHttpRequest request = exchange.getRequest();
            SecurityContextHolder.getContext().setAuthentication(auth);
            Mono<Boolean> isPermission = super.hasPermission(auth, request.getMethodValue(), request.getURI().getPath());
            SecurityContextHolder.clearContext();
            return isPermission.map(granted -> new AuthorizationDecision(granted));
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }
    @Override
    public Mono<List<SysMenu>> findMenuByRoleCodes(String tenant ,String roleCodes) {
        Mono<List<SysMenu>> result = asyncMenuFeignClient.findByRoleCodes(tenant,roleCodes);
        try {
            return result;
        } catch (Exception e) {
            log.error("asynMenuService.findMenuByRoleCodes-error", e);
        }
        return Mono.just(Collections.emptyList());
    }
}