package com.open.capacity.ext.iop.feign;

import com.open.capacity.ext.iop.IopClient;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@IopClient(clientBuilder = FeignIopBuilder.class,configAnnotation = FeignIopClient.class,definer = FeignIopClientsDefiner.class)
public @interface FeignIopClient {
    @AliasFor(annotation = IopClient.class)
    Class<?>[] configuration() default {};
    String name() default "";
    String url() default "";
    boolean decode404() default false;
    Class<?> fallback() default void.class;
    Class<?> fallbackFactory() default void.class;
    String path() default "";
}
