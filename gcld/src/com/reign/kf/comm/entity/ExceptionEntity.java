package com.reign.kf.comm.entity;

public class ExceptionEntity
{
    public String msg;
    
    public ExceptionEntity(final String message) {
        this.msg = message;
    }
    
    public String getMsg() {
        return this.msg;
    }
    
    public void setMsg(final String msg) {
        this.msg = msg;
    }
}
