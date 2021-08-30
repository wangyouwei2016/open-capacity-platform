package com.open.capacity.common.tenant;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.open.capacity.common.util.StringUtil;
import com.open.capacity.common.util.TokenUtil;
import com.open.capacity.datasource.constant.Global;
import com.open.capacity.datasource.props.TenantProperties;
import com.open.capacity.datasource.util.TenantSqlParserExt;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;

/**
 * MybatsPlus配置类 Created by dell on 2019/1/10.
 * 
 * @author dell
 */
@Configuration
@EnableConfigurationProperties(TenantProperties.class)
public class TenantAutoConfig {

	@Autowired
	private TenantProperties tenantProperties;

	/**
	 * 分页插件
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor page = new PaginationInterceptor();
		if (tenantProperties.isEnabled()) {
			initTenantHandler(page);
		}
		return page;
	}

	/**
	 * 初始化多租户处理器
	 * 
	 * @param paginationInterceptor
	 */
	public void initTenantHandler(PaginationInterceptor paginationInterceptor) {
		ArrayList<ISqlParser> sqlParsers = new ArrayList<>();
		TenantSqlParser tenantSqlParser = new TenantSqlParserExt();
		tenantSqlParser.setTenantHandler(new TenantHandler() {
			@Override
			public Expression getTenantId(boolean where) {
				//强认证后获取应用区分租户
				String clientId = TokenUtil.getClientId();

				if (StringUtil.isNotEmpty(clientId)) {
					return new StringValue(clientId);
				}

				return new NullValue();
			}

			@Override
			public String getTenantIdColumn() {
				return Global.TENANT_ID;
			}

			@Override
			public boolean doTableFilter(String tableName) {
				
				return tenantProperties.getNotTenantTables().stream().anyMatch((e) -> e.equalsIgnoreCase(tableName));
			}
		});
		sqlParsers.add(tenantSqlParser);
		paginationInterceptor.setSqlParserList(sqlParsers);
	}

}
