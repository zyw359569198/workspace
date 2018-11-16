package com.reign.framework.netty.mvc.result;

public interface Result<T>
{
    String getViewName();
    
    T getResult();
}
