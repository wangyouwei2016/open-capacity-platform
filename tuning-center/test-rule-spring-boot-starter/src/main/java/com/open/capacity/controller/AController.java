package com.open.capacity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.service.GroovyLoaderService;
import com.open.capacity.service.TestService;
import com.open.capacity.service.WeatherService;

@RestController
public class AController {

	@Autowired
	private WeatherService service ;
	
	@Autowired
	private GroovyLoaderService groovyLoader ;
 
	
	@GetMapping("/hello")
	public String exec() {
		service.executeRule( );
		return "hello";
	}
	
	@GetMapping("/add")
	private String add() {
		TestService testService = groovyLoader.getApplicationContext().getBean(TestService.class);
		testService.add();
		return "add";
	}
	@GetMapping("/put")
	public String put() {
		groovyLoader.getBean("weatherRule", " package com.open.capacity.rule;\r\n"
				+ "\r\n"
				+ "import com.open.capacity.dto.WeatherDto;\r\n"
				+ "import com.open.capacity.rule.annocation.RuleGroup;\r\n"
				+ "import com.ql.util.express.DefaultContext;\r\n"
				+ "import com.ql.util.express.ExpressRunner;\r\n"
				+ "\r\n"
				+ "import org.jeasy.rules.annotation.Action;\r\n"
				+ "import org.jeasy.rules.annotation.Condition;\r\n"
				+ "import org.jeasy.rules.annotation.Fact;\r\n"
				+ "import org.jeasy.rules.annotation.Rule;\r\n"
				+ "\r\n"
				+ "@RuleGroup(name = \"weather rule\")\r\n"
				+ "@Rule(name = \"weather rule\", description = \"if it rains then take an umbrella\", priority = 3)\r\n"
				+ "public class WeatherRule  {\r\n"
				+ "\r\n"
				+ "	\r\n"
				+ "	\r\n"
				+ "	@Condition\r\n"
				+ "    public boolean itRains(@Fact(EasyRulesTemplate.DEFAULT_FACT_NAME)  WeatherDto weatherDto) {\r\n"
				+ "		\r\n"
				+ "		String express = weatherDto.getExpress();\r\n"
				+ "		ExpressRunner runner = new ExpressRunner();\r\n"
				+ "		DefaultContext<String, Object> context = new DefaultContext<String, Object>();\r\n"
				+ "		context.put(\"a\", weatherDto.getValue());\r\n"
				+ "		Boolean r = false;\r\n"
				+ "		try {\r\n"
				+ "			r = (Boolean) runner.execute(express, context, null, true, false);\r\n"
				+ "		} catch (Exception e) {\r\n"
				+ "		}\r\n"
				+ "        return r.booleanValue();\r\n"
				+ "    }\r\n"
				+ "    \r\n"
				+ "    @Action\r\n"
				+ "    public void takeAnUmbrella(@Fact(EasyRulesTemplate.DEFAULT_FACT_NAME)  WeatherDto weatherDto) {\r\n"
				+ "       System.out.println(weatherDto);\r\n"
				+ "    }\r\n"
				+ " \r\n"
				+ "} ") ;
		return "ok";
	}
}
