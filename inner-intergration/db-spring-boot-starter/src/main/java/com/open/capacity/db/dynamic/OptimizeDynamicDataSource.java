/**
 * 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
 */
package com.open.capacity.db.dynamic;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.core.InfrastructureProxy;

public class OptimizeDynamicDataSource extends DynamicRoutingDataSource implements InfrastructureProxy {
    @Override
    public Object getWrappedObject() {
        return determineDataSource();
    }
}
