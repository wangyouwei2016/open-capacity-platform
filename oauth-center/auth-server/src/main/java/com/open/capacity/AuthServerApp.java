/**
 * 
 */
package com.open.capacity;

import com.open.capacity.common.feign.GlobalFeignConfig;
import com.open.capacity.common.port.PortApplicationEnvironmentPreparedEventListener;
import com.open.capacity.log.annotation.EnableLogging;
import com.open.capacity.uaa.server.UAAServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/** 
* @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
* 类说明 
*/
@EnableLogging
@SpringBootApplication
@Import(UAAServerConfig.class)
@EnableFeignClients(defaultConfiguration= GlobalFeignConfig.class)
public class AuthServerApp {
	
	public static void main(String[] args) {
//		固定端口启动
//		SpringApplication.run(OpenAuthServerApp.class, args);
		
		//随机端口启动
		SpringApplication app = new SpringApplication(AuthServerApp.class);
        app.addListeners(new PortApplicationEnvironmentPreparedEventListener());
        app.run(args);
		
	}

}
