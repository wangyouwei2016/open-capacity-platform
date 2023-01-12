package com.open.capacity.rule.autoconfigure;

import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineListener;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.capacity.rule.EasyRulesConfig;
import com.open.capacity.rule.EasyRulesTemplate;

 
@Configuration
@EnableConfigurationProperties(EasyRulesConfig.class)
@ConditionalOnProperty(prefix = "easy.rules", name = "enabled", matchIfMissing = true)
public class EasyRulesAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "easy.rules", name = "rules-engine-type", havingValue = "default" ,matchIfMissing = true)
    @ConditionalOnMissingBean(RulesEngine.class)
    public RulesEngine defaultRulesEngine( EasyRulesConfig easyRulesConfig, ObjectProvider<RuleListener> ruleListeners, ObjectProvider<RulesEngineListener> rulesEngineListeners) {
        DefaultRulesEngine engine = new DefaultRulesEngine(
            new RulesEngineParameters()
                .priorityThreshold(easyRulesConfig.getRulePriorityThreshold())
                .skipOnFirstAppliedRule(easyRulesConfig.isSkipOnFirstAppliedRule())
                .skipOnFirstFailedRule(easyRulesConfig.isSkipOnFirstFailedRule())
                .skipOnFirstNonTriggeredRule(easyRulesConfig.isSkipOnFirstNonTriggeredRule())
        );
        ruleListeners.orderedStream().forEach(engine::registerRuleListener);
        rulesEngineListeners.orderedStream().forEach(engine::registerRulesEngineListener);
        return engine;
    }


    @Bean
    @ConditionalOnProperty(prefix = "easy.rules", name = "rules-engine-type", havingValue = "inference")
    @ConditionalOnMissingBean(RulesEngine.class)
    public RulesEngine inferenceRulesEngine(EasyRulesConfig easyRulesConfig, ObjectProvider<RuleListener> ruleListeners, ObjectProvider<RulesEngineListener> rulesEngineListeners) {
        InferenceRulesEngine engine = new InferenceRulesEngine(
            new RulesEngineParameters()
                .priorityThreshold(easyRulesConfig.getRulePriorityThreshold())
                .skipOnFirstAppliedRule(easyRulesConfig.isSkipOnFirstAppliedRule())
                .skipOnFirstFailedRule(easyRulesConfig.isSkipOnFirstFailedRule())
                .skipOnFirstNonTriggeredRule(easyRulesConfig.isSkipOnFirstNonTriggeredRule())
        );
        ruleListeners.orderedStream().forEach(engine::registerRuleListener);
        rulesEngineListeners.orderedStream().forEach(engine::registerRulesEngineListener);
        return engine;
    }

    @Bean
    @ConditionalOnBean(RulesEngine.class)
    public EasyRulesTemplate easyRulesTemplate(RulesEngine rulesEngine, ApplicationContext applicationContext) {
        return new EasyRulesTemplate(rulesEngine, applicationContext);
    }
}
