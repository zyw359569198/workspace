package com.reign.framework.exception;

public class IlleageArgumentException extends BaseException
{
    private static final long serialVersionUID = -976247678578052776L;
    
    public IlleageArgumentException() {
    }
    
    public IlleageArgumentException(final String errorCode) {
        super(errorCode);
    }
    
    public IlleageArgumentException(final String errorCode, final Object... args) {
        super(errorCode, args);
    }
    
    public IlleageArgumentException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
    }
    
    public IlleageArgumentException(final String errorCode, final Throwable throwable, final Object... args) {
        super(errorCode, throwable, args);
    }
}
