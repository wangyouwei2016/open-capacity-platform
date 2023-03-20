package com.open.capacity.ext.mvc.decrypt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestDec {
    /**
     * mvc请求解密策略
     * @return
     */
    String decType();
}
