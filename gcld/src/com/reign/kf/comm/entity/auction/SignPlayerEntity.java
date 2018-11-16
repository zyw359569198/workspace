package com.reign.kf.comm.entity.auction;

public class SignPlayerEntity
{
    public static final int SUCC = 1;
    private int auctionId;
    private int state;
    private int errorCode;
    
    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setAuctionId(final int auctionId) {
        this.auctionId = auctionId;
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
