package com.reign.framework.netty.mvc.annotation;

public @interface Rule {
    Class<? extends com.reign.framework.netty.mvc.validation.Rule<?>> rule();
    
    String expression();
}
