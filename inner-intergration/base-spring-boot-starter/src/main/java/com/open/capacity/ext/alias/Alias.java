package com.open.capacity.ext.alias;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Aliases.class)
@Component
public @interface Alias {
    String[] value() default {};
    String groupName() default BeansAliasUtils.DEF_GROUP_NAME;
}
