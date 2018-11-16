package com.reign.kf.comm.exception;

public class TypeRegisterException extends RuntimeException
{
    private static final long serialVersionUID = 509120453445351282L;
    
    public TypeRegisterException() {
    }
    
    public TypeRegisterException(final String msg) {
        super(msg);
    }
}
