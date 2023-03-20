package com.open.capacity.db.dynamic;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.core.InfrastructureProxy;

public class OptimizeDynamicDataSource extends DynamicRoutingDataSource implements InfrastructureProxy {
    @Override
    public Object getWrappedObject() {
        return determineDataSource();
    }
}
