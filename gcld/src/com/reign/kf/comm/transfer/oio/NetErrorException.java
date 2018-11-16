package com.reign.kf.comm.transfer.oio;

public class NetErrorException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public NetErrorException() {
    }
    
    public NetErrorException(final String errorCode, final Throwable throwable) {
        super(errorCode, throwable);
    }
    
    public NetErrorException(final String errorCode) {
        super(errorCode);
    }
    
    public NetErrorException(final Throwable throwable) {
        super(throwable);
    }
}
