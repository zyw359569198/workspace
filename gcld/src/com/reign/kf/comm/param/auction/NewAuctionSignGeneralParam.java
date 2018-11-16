package com.reign.kf.comm.param.auction;

public class NewAuctionSignGeneralParam
{
    private int auctionId;
    private int generalId;
    private int generalLv;
    private int leader;
    private int forces;
    private int intelligence;
    private int politics;
    private int growNum;
    private int auctionFailTimes;
    
    public int getPolitics() {
        return this.politics;
    }
    
    public void setPolitics(final int politics) {
        this.politics = politics;
    }
    
    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setAuctionId(final int auctionId) {
        this.auctionId = auctionId;
    }
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public int getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final int generalLv) {
        this.generalLv = generalLv;
    }
    
    public int getLeader() {
        return this.leader;
    }
    
    public void setLeader(final int leader) {
        this.leader = leader;
    }
    
    public int getForces() {
        return this.forces;
    }
    
    public void setForces(final int forces) {
        this.forces = forces;
    }
    
    public int getIntelligence() {
        return this.intelligence;
    }
    
    public void setIntelligence(final int intelligence) {
        this.intelligence = intelligence;
    }
    
    public int getGrowNum() {
        return this.growNum;
    }
    
    public void setGrowNum(final int growNum) {
        this.growNum = growNum;
    }
    
    public int getAuctionFailTimes() {
        return this.auctionFailTimes;
    }
    
    public void setAuctionFailTimes(final int auctionFailTimes) {
        this.auctionFailTimes = auctionFailTimes;
    }
}
