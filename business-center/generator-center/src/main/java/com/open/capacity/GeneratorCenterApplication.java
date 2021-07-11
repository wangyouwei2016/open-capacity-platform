package com.open.capacity;

import com.open.capacity.log.annotation.EnableLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
/**
 * 启动类
 * @Author: [dawei QQ:64738479]
 * @Date: [2019-04-25 21:48]
 * @Description: [ ]
 * @Version: [1.0.1]
 * @Copy: [com.zzg]
 */
@Configuration
@EnableLogging
@SpringBootApplication
public class GeneratorCenterApplication {
	public static void main(String[] args) {
		SpringApplication.run(GeneratorCenterApplication.class, args);
	}
}
