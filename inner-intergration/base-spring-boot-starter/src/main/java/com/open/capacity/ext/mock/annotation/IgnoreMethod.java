/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreMethod {
}
