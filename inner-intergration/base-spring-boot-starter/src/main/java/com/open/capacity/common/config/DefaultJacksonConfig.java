package com.open.capacity.common.config;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.open.capacity.common.constant.CommonConstant;

/**
 * 处理LocalDate、LocalDateTime、Long 三种类型JSON序列化
 **/
@Configuration
public class DefaultJacksonConfig {
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer dateCustomizer() {
	   return builder -> {
	       SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_TIME_FORMAT_PATTERN);
	       builder.serializerByType(Date.class, new DateSerializer(false, dateFormat));
	       builder.deserializerByType(Date.class, new DateDeserializers.DateDeserializer(DateDeserializers.DateDeserializer.instance, dateFormat, CommonConstant.DATE_TIME_FORMAT_PATTERN));
	   };
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer localDateCustomizer() {
		return builder -> {
	        builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT_PATTERN)));
	        builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT_PATTERN)));
	    };
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
		return builder -> {
	        builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonConstant.DATE_TIME_FORMAT_PATTERN)));
	        builder.deserializerByType(LocalDateTime.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(CommonConstant.DATE_TIME_FORMAT_PATTERN)));
	    };
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer localTimeCustomizer() {
		return builder -> {
	        builder.serializerByType(LocalTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonConstant.TIME_FORMAT_PATTERN)));
	        builder.deserializerByType(LocalTime.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(CommonConstant.TIME_FORMAT_PATTERN)));
	    };
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer longCustomizer() {
		return builder -> { 
			builder.serializerByType(Long.class, ToStringSerializer.instance) ;
			builder.modules(new JavaTimeModule());
		};
	}
}
