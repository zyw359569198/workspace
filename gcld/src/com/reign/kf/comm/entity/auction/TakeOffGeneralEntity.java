package com.reign.kf.comm.entity.auction;

public class TakeOffGeneralEntity
{
    public static final int SUCC = 1;
    public static final int ERROR = 2;
    public static final int ERROR_NO_GENERAL = -1;
    public static final int ERROR_IN_AUCTION = -2;
    private int state;
    private int errorCode;
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
}
