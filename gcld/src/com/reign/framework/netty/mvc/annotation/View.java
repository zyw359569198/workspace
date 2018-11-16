package com.reign.framework.netty.mvc.annotation;

import java.lang.annotation.*;
import com.reign.framework.netty.mvc.view.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface View {
    String name();
    
    Class<? extends ResponseView> type();
    
    String compress() default "";
}
