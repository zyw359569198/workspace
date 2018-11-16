package com.reign.kf.comm.entity.auction;

public class AuctionSignGeneralEntity
{
    public static final int SUCC = 1;
    public static final int ERROR = 2;
    public static final int ERROR_NO_SIGN = -1;
    public static final int ERROR_EXISTS = -2;
    public static final int ERROR_WRONGGENERAL = -3;
    private int id;
    private int state;
    private int errorCode;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
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
