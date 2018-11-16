package com.reign.kf.comm.entity.auction;

import java.util.*;

public class AuctionGeneralEntity
{
    public static final int GENERAL_STATE_0 = 0;
    public static final int GENERAL_STATE_1 = 1;
    public static final int GENERAL_STATE_2 = 2;
    public static final int GENERAL_STATE_3 = 3;
    private int id;
    private PlayerEntity auctionPlayer;
    private int generalId;
    private int generalLv;
    private int leader;
    private int forces;
    private int intelligence;
    private int growNum;
    private int price;
    private PlayerEntity lastBuyer;
    private int state;
    private long cd;
    private boolean count;
    private Date startTime;
    private Date endTime;
    private int version;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public PlayerEntity getAuctionPlayer() {
        return this.auctionPlayer;
    }
    
    public void setAuctionPlayer(final PlayerEntity auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
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
    
    public int getPrice() {
        return this.price;
    }
    
    public void setPrice(final int price) {
        this.price = price;
    }
    
    public PlayerEntity getLastBuyer() {
        return this.lastBuyer;
    }
    
    public void setLastBuyer(final PlayerEntity lastBuyer) {
        this.lastBuyer = lastBuyer;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public long getCd() {
        return this.cd;
    }
    
    public void setCd(final long cd) {
        this.cd = cd;
    }
    
    public boolean isCount() {
        return this.count;
    }
    
    public void setCount(final boolean count) {
        this.count = count;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
}
