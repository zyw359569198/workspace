package com.reign.framework.exception;

public class BusinessException extends BaseException
{
    private static final long serialVersionUID = -976247678578052776L;
    
    public BusinessException() {
    }
    
    public BusinessException(final String errorCode) {
        super(errorCode);
    }
    
    public BusinessException(final String errorCode, final Object... args) {
        super(errorCode, args);
    }
    
    public BusinessException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
    }
    
    public BusinessException(final String errorCode, final Throwable throwable, final Object... args) {
        super(errorCode, throwable, args);
    }
}
