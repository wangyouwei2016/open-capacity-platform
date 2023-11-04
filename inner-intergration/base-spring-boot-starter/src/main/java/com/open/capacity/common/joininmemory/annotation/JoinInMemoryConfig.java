package com.open.capacity.common.joininmemory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by taoli on 2022/8/5.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemoryConfig {
    JoinInMemeoryExecutorType executorType() default JoinInMemeoryExecutorType.SERIAL;
    String executorName() default "defaultExecutor";
}
