package com.reign.plugin.yx.util;

public class AESException extends Exception
{
    public AESException(final Exception e) {
        super(e);
    }
    
    public AESException() {
    }
    
    public AESException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AESException(final Throwable cause) {
        super(cause);
    }
}
