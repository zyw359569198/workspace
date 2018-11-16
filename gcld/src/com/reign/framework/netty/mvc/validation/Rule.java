package com.reign.framework.netty.mvc.validation;

public interface Rule<T>
{
    T getRule();
    
    void parse(final String p0);
}
