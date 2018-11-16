package com.reign.framework.exception;

public class InternalException extends BaseException
{
    private static final long serialVersionUID = -976247678578052776L;
    
    public InternalException() {
    }
    
    public InternalException(final String errorCode) {
        super(errorCode);
    }
    
    public InternalException(final String errorCode, final Object... args) {
        super(errorCode, args);
    }
    
    public InternalException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
    }
    
    public InternalException(final String errorCode, final Throwable throwable, final Object... args) {
        super(errorCode, throwable, args);
    }
}
