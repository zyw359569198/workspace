package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;

public class KfwdOK implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String info;
    
    public KfwdOK() {
    }
    
    public KfwdOK(final String info) {
        this.info = info;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
}
