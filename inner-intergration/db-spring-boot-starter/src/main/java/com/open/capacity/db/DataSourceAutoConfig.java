package com.open.capacity.db;


import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;


/**
 * @author 作者 someday 
 * @version 创建时间：2017年04月23日 下午20:01:06 类说明
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 * 在设置了spring.datasource.enable.dynamic 等于true是开启多数据源，配合日志
 */
@Configuration
@AutoConfigureBefore(value={DruidDataSourceAutoConfigure.class,MybatisPlusAutoConfiguration.class})
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "false")
public class DataSourceAutoConfig {
 
//	创建数据源
	@Bean
	@ConfigurationProperties("spring.datasource.druid")
	public DataSource dataSourceCore(){
	    return DruidDataSourceBuilder.create().build();
	}
 
   
}
