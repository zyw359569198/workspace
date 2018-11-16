package com.reign.framework.exception;

import com.reign.util.*;

public abstract class BaseException extends RuntimeException
{
    private static final long serialVersionUID = -7491198442886821304L;
    private String errorCode;
    private Object[] args;
    
    public BaseException() {
    }
    
    public BaseException(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public BaseException(final String errorCode, final Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public BaseException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
        this.errorCode = errorCode;
    }
    
    public BaseException(final String errorCode, final Throwable throwable, final Object... args) {
        super(errorCode, throwable);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    @Override
    public String getMessage() {
        return this.errorCode;
    }
    
    public String getConvertedMessage() {
        final String message = PropertiesUtil.getText(BaseException.class, this.errorCode);
        if (this.args == null) {
            return message;
        }
        return MessageFormatter.format(message, this.args);
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public void setArgs(final Object... args) {
        this.args = args;
    }
    
    public Object[] getArgs() {
        return this.args;
    }
}
