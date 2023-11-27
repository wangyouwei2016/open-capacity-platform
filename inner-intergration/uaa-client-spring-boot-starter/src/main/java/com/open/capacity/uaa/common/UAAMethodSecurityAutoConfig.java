package com.open.capacity.uaa.common;

import com.open.capacity.uaa.common.handler.CustomMethodSecurityExpressionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * @author lzw
 * @description 条件判断是否开启业务服务的方法级别权限校验
 * 当网关透传token时开启@EnableGlobalMethodSecurity(prePostEnabled = true)
 * @date 2023/11/27 13:37
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureBefore(OAuth2AutoConfiguration.class)
@ConditionalOnProperty(prefix = "ocp.security.auth",value = "isPassAuth", havingValue = "true", matchIfMissing = false) //判断token透传开关
public class UAAMethodSecurityAutoConfig {

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Configuration
    public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            return new CustomMethodSecurityExpressionHandler();
        }
    }
}
