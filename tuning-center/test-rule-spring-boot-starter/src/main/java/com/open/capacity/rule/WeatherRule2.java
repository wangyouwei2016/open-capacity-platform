package com.open.capacity.rule;

import com.open.capacity.dto.WeatherDto;
import com.open.capacity.rule.annocation.RuleGroup;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@RuleGroup(name = "weather rule")
@Rule(name = "weather rule", description = "if it rains then take an umbrella", priority = 2)
public class WeatherRule2 {
	
	@Condition
    public boolean itRains(@Fact(EasyRulesTemplate.DEFAULT_FACT_NAME)  WeatherDto weatherDto) {
        return true;
    }
    
    @Action
    public void takeAnUmbrella() {
        System.out.println("It rains, take an umbrella2!");
    }
 
}