package com.open.capacity.rabbitmq.comsumer;


import com.open.capacity.rabbitmq.annotation.FastRabbitListener;

import java.lang.annotation.*;


/**
 * @author liuchunqing
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FastRabbitListens {
    FastRabbitListener[] value();
}
