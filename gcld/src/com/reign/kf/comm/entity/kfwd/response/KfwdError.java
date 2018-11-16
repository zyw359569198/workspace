package com.reign.kf.comm.entity.kfwd.response;

public class KfwdError
{
    private static final long serialVersionUID = 1L;
    private String message;
    
    public KfwdError() {
        this(null);
    }
    
    public KfwdError(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
