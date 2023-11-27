package com.open.capacity.uaa.common.handler;

import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysRole;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lzw
 * @description 自定义Security方法权限表达式处理器，当@PreAuthorize注解判断权限时，合并用户的角色和权限
 * 由于
 * （1）DefaultMethodSecurityExpressionHandler 通过 UserDetails#getAuthorities() 方法获取用户权限（角色和权限）。
 * （2）LoginAppUser#getAuthorities() 方法自定义只返回角色（role code）。
 * 因此无法满足默认的 @PreAuthorize(hasAuthority()) 注解使用条件，通过自定义RoleHierarchy将用户的角色和权限合并。
 * @date 2023/11/27 13:37
 */
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.getTrustResolver());
        // 自定义角色层次实现，合并用户角色和权限
        root.setRoleHierarchy(new CustomRoleHierarchyImpl(this.getDefaultRolePrefix()));
        root.setDefaultRolePrefix(this.getDefaultRolePrefix());
        return root;
    }

    /**
     * 自定义角色层次实现，合并用户角色和权限
     */
    class CustomRoleHierarchyImpl extends RoleHierarchyImpl {

        private String rolePrefix = "";

        public CustomRoleHierarchyImpl() {
        }

        public CustomRoleHierarchyImpl(String rolePrefix) {
            this.rolePrefix = rolePrefix;
        }

        @Override
        public Collection<GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
            Collection<GrantedAuthority> collection = SynchronizedCollection.synchronizedCollection(new HashSet<>());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginAppUser) {
                LoginAppUser currentUser = (LoginAppUser)authentication.getPrincipal();
                List<SysRole> roles = currentUser.getRoles();
                Set<String> permissions = currentUser.getPermissions();

                if (!CollectionUtils.isEmpty(roles)) {
                    roles.parallelStream()
                            .forEach(role -> collection.add(new SimpleGrantedAuthority(rolePrefix + role.getCode())));
                }
                if (!CollectionUtils.isEmpty(permissions)) {
                    permissions.parallelStream()
                            .forEach(permission -> collection.add(new SimpleGrantedAuthority(permission)));
                }
            }
            return collection;
        }
    }

    /**
     * 自定义SecurityExpressionRoot，因为MethodSecurityExpressionRoot无法调用构造器，也无法继承，因此复制了该类
     */
    class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
        private Object filterObject;
        private Object returnObject;
        private Object target;

        CustomMethodSecurityExpressionRoot(Authentication a) {
            super(a);
        }
        @Override
        public void setFilterObject(Object filterObject) {
            this.filterObject = filterObject;
        }

        @Override
        public Object getFilterObject() {
            return this.filterObject;
        }

        @Override
        public void setReturnObject(Object returnObject) {
            this.returnObject = returnObject;
        }

        @Override
        public Object getReturnObject() {
            return this.returnObject;
        }

        void setThis(Object target) {
            this.target = target;
        }

        @Override
        public Object getThis() {
            return this.target;
        }
    }
}
