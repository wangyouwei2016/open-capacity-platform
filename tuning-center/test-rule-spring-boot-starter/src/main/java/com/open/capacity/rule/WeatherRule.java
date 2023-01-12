//package com.open.capacity.rule;
//
//import com.open.capacity.dto.WeatherDto;
//import com.open.capacity.rule.annocation.RuleGroup;
//import com.ql.util.express.DefaultContext;
//import com.ql.util.express.ExpressRunner;
//
//import org.jeasy.rules.annotation.Action;
//import org.jeasy.rules.annotation.Condition;
//import org.jeasy.rules.annotation.Fact;
//import org.jeasy.rules.annotation.Rule;
//
//@RuleGroup(name = "weather rule")
//@Rule(name = "weather rule", description = "if it rains then take an umbrella", priority = 3)
//public class WeatherRule  {
//
//	
//	
//	@Condition
//    public boolean itRains(@Fact(EasyRulesTemplate.DEFAULT_FACT_NAME)  WeatherDto weatherDto) {
//		
//		String express = weatherDto.getExpress();
//		ExpressRunner runner = new ExpressRunner();
//		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
//		context.put("a", weatherDto.getValue());
//		Boolean r = false;
//		try {
//			r = (Boolean) runner.execute(express, context, null, true, false);
//		} catch (Exception e) {
//		}
//        return r.booleanValue();
//    }
//    
//    @Action
//    public void takeAnUmbrella(@Fact(EasyRulesTemplate.DEFAULT_FACT_NAME)  WeatherDto weatherDto) {
//       System.out.println(weatherDto);
//    }
// 
//}