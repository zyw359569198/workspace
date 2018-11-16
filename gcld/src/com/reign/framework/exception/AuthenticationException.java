package com.reign.framework.exception;

public class AuthenticationException extends BaseException
{
    private static final long serialVersionUID = -976247678578052776L;
    
    public AuthenticationException() {
    }
    
    public AuthenticationException(final String errorCode) {
        super(errorCode);
    }
    
    public AuthenticationException(final String errorCode, final Object... args) {
        super(errorCode, args);
    }
    
    public AuthenticationException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
    }
    
    public AuthenticationException(final String errorCode, final Throwable throwable, final Object... args) {
        super(errorCode, throwable, args);
    }
}
