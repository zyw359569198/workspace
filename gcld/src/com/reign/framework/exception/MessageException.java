package com.reign.framework.exception;

public class MessageException extends BaseException
{
    private static final long serialVersionUID = -976247678578052776L;
    
    public MessageException() {
    }
    
    public MessageException(final String errorCode) {
        super(errorCode);
    }
    
    public MessageException(final String errorCode, final Object... args) {
        super(errorCode, args);
    }
    
    public MessageException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
    }
    
    public MessageException(final String errorCode, final Throwable throwable, final Object... args) {
        super(errorCode, throwable, args);
    }
}
