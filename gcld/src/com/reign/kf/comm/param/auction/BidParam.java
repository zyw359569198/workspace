package com.reign.kf.comm.param.auction;

public class BidParam
{
    private int id;
    private int price;
    private int auctionId;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getPrice() {
        return this.price;
    }
    
    public void setPrice(final int price) {
        this.price = price;
    }
    
    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setAuctionId(final int auctionId) {
        this.auctionId = auctionId;
    }
}
