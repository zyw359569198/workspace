package com.reign.framework.exception;

public class ServletConfigException extends RuntimeException
{
    private static final long serialVersionUID = -1607861734737042930L;
    
    public ServletConfigException(final String msg) {
        super(msg);
    }
    
    public ServletConfigException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
