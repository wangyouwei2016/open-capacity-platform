package com.open.capacity.rule.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RuleGroup {

    /**
     * bean bean alias
     * @return alias
     */
    String value() default "";

    /**
     * group name
     * @return name
     */
    String name();
    
}
