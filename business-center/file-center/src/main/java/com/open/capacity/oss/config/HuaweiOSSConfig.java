package com.open.capacity.oss.config;

import com.obs.services.ObsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 华为云配置
 */
@Configuration
public class HuaweiOSSConfig {

	@Value("${obs.endpoint}")
	private String endpoint;
	@Value("${obs.ak}")
	private String accessKeyId;
	@Value("${obs.sk}")
	private String accessKeySecret;

	/**
	 * 华为云文件存储client
	 * 只有配置了aliyun.oss.access-key才可以使用
	 */
	@Bean
	@ConditionalOnProperty(name = "obs.ak", matchIfMissing = true)
	public ObsClient ossClient() {
		ObsClient obsClient = new ObsClient(accessKeyId, accessKeySecret, endpoint);

		return obsClient;
	}

}
