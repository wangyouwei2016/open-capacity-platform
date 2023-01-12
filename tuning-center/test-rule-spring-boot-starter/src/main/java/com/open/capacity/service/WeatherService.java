package com.open.capacity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.open.capacity.dto.WeatherDto;
import com.open.capacity.rule.EasyRulesTemplate;

@Service
public class WeatherService {
    // 引入 easyRulesTemplate
	@Autowired(required=false)
    private  EasyRulesTemplate easyRulesTemplate;
	
    
    public void executeRule( ) {
    	
     
        // 触发指定的规则组
//    	
//    	WeatherRule weatherRule  = new WeatherRule();
//    	
//    	
//    	UnitRuleGroup myUnitRuleGroup = new UnitRuleGroup("myUnitRuleGroup", "unit of myRule1 and myRule2");
//    	myUnitRuleGroup.addRule(weatherRule);
// 
//    	
//    	WeatherRule2 WeatherRule2 = new WeatherRule2();
//    	
//    	Rules rules = new Rules();
//    	rules.register(myUnitRuleGroup);
//    	rules.register(WeatherRule2);
     
    	WeatherDto dto = new WeatherDto();
    	
    	dto.setName("a");
    	dto.setValue(1);
    	dto.setExpress("a> 0");
    	
//        this.easyRulesTemplate.fire(rules,EasyRulesTemplate.DEFAULT_FACT_NAME, dto);
        
        easyRulesTemplate.fire("weather rule",EasyRulesTemplate.DEFAULT_FACT_NAME, dto);
    }
}
