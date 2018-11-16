package com.reign.framework.netty.mvc.exception;

public class NotMatchResultException extends RuntimeException
{
    private static final long serialVersionUID = -1570607388478971788L;
    
    public NotMatchResultException(final String message, final Class<?> clazz) {
        super(String.valueOf(message) + clazz.getName());
    }
}
