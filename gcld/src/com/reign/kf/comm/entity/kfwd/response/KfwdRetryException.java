package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;

public class KfwdRetryException implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String message;
    
    public KfwdRetryException() {
        this(null);
    }
    
    public KfwdRetryException(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
