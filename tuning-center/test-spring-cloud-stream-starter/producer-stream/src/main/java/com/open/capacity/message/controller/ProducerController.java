package com.open.capacity.message.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.open.capacity.message.service.ProducerService;

@RestController
public class ProducerController {

	@Resource
	private ProducerService producerService ;
	
	@GetMapping("/nomal")
	public boolean sayHello() {
		Map map = Maps.newHashMap();
		map.put("id", 1);
		return producerService.sendMsg(map) ;
	}
	
	@GetMapping("/delay")
	public boolean sayDelay() {
		Map map = Maps.newHashMap();
		map.put("id", 1);
		return producerService.sendDelayMsg(map) ;
	}
	
	@GetMapping("/order")
	public boolean sayOrder() {
		
		for(int i = 0 ; i <=10 ;i ++) {
			Map map = Maps.newHashMap();
			map.put("id", i);
			producerService.sendOrderMsg(map) ;
		}
		
		return true ;
	}
	
	
	
	@GetMapping("/tx")
	public boolean sayWorld(@RequestParam String version) {
		return producerService.sendTransactionMessage(version) ;
	}
}
