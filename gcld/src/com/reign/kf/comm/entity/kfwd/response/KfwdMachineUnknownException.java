package com.reign.kf.comm.entity.kfwd.response;

public class KfwdMachineUnknownException extends KfwdRetryException
{
    private static final long serialVersionUID = 1L;
    public static final KfwdMachineUnknownException SINGLETON;
    
    static {
        SINGLETON = new KfwdMachineUnknownException();
    }
}
