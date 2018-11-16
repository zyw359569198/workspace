package com.reign.framework.netty.mvc.result;

public class ExceptionResult implements Result<Throwable>
{
    private Throwable t;
    
    public ExceptionResult(final Throwable t) {
        this.t = t;
    }
    
    @Override
    public String getViewName() {
        return "exception";
    }
    
    @Override
    public Throwable getResult() {
        return this.t;
    }
}
