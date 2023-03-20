package com.open.capacity.ext.iop;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Iop
public @interface IopClient {
    Class<? extends IopClientBuilder> clientBuilder();
    Class<? extends IopClientsDefiner> definer() default AutoFieldClientDefiner.class;
    Class<? extends Annotation> configAnnotation() default IopClient.class ;
    String beanAlias () default "";
    Class<?>[] configuration() default {};
}
