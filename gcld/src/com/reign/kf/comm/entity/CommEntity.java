package com.reign.kf.comm.entity;

public class CommEntity
{
    public static final int SUCC = 1;
    public static final int ERROR = 2;
    public static final CommEntity SUCC_ENTITY;
    public static final CommEntity ERROR_ENTITY;
    private int state;
    private String message;
    
    static {
        SUCC_ENTITY = new CommEntity();
        CommEntity.SUCC_ENTITY.state = 1;
        ERROR_ENTITY = new CommEntity();
        CommEntity.ERROR_ENTITY.state = 2;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
