package com.reign.framework.netty.mvc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface Validation {
    Class<? extends com.reign.framework.netty.mvc.validation.Validation> handler();
    
    Rule rule();
}
