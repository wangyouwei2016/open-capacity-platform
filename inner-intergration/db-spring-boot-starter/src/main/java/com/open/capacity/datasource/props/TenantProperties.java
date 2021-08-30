package com.open.capacity.datasource.props;

import java.util.TreeSet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author weicong
 * @version 1.0 createTime 2019/1/10.
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.tenant")
public class TenantProperties {

    private boolean enabled = false ;
    private TreeSet<String> notTenantTables;

    
}
