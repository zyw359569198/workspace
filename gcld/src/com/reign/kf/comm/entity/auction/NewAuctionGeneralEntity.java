package com.reign.kf.comm.entity.auction;

import java.util.*;

public class NewAuctionGeneralEntity extends AuctionGeneralEntity
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
    private int politics;
    private int growNum;
    private int price;
    private PlayerEntity lastBuyer;
    private int state;
    private long cd;
    private boolean count;
    private Date startTime;
    private Date endTime;
    private int bidTimes;
    private int version;
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void setId(final int id) {
        this.id = id;
    }
    
    @Override
    public PlayerEntity getAuctionPlayer() {
        return this.auctionPlayer;
    }
    
    @Override
    public void setAuctionPlayer(final PlayerEntity auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
    }
    
    @Override
    public int getGeneralId() {
        return this.generalId;
    }
    
    @Override
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    @Override
    public int getGeneralLv() {
        return this.generalLv;
    }
    
    @Override
    public void setGeneralLv(final int generalLv) {
        this.generalLv = generalLv;
    }
    
    @Override
    public int getLeader() {
        return this.leader;
    }
    
    @Override
    public void setLeader(final int leader) {
        this.leader = leader;
    }
    
    @Override
    public int getForces() {
        return this.forces;
    }
    
    @Override
    public void setForces(final int forces) {
        this.forces = forces;
    }
    
    @Override
    public int getIntelligence() {
        return this.intelligence;
    }
    
    @Override
    public void setIntelligence(final int intelligence) {
        this.intelligence = intelligence;
    }
    
    @Override
    public int getGrowNum() {
        return this.growNum;
    }
    
    @Override
    public void setGrowNum(final int growNum) {
        this.growNum = growNum;
    }
    
    @Override
    public int getPrice() {
        return this.price;
    }
    
    @Override
    public void setPrice(final int price) {
        this.price = price;
    }
    
    @Override
    public PlayerEntity getLastBuyer() {
        return this.lastBuyer;
    }
    
    @Override
    public void setLastBuyer(final PlayerEntity lastBuyer) {
        this.lastBuyer = lastBuyer;
    }
    
    @Override
    public int getState() {
        return this.state;
    }
    
    @Override
    public void setState(final int state) {
        this.state = state;
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public void setVersion(final int version) {
        this.version = version;
    }
    
    @Override
    public long getCd() {
        return this.cd;
    }
    
    @Override
    public void setCd(final long cd) {
        this.cd = cd;
    }
    
    @Override
    public boolean isCount() {
        return this.count;
    }
    
    @Override
    public void setCount(final boolean count) {
        this.count = count;
    }
    
    @Override
    public Date getStartTime() {
        return this.startTime;
    }
    
    @Override
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    @Override
    public Date getEndTime() {
        return this.endTime;
    }
    
    @Override
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public int getBidTimes() {
        return this.bidTimes;
    }
    
    public void setBidTimes(final int bidTimes) {
        this.bidTimes = bidTimes;
    }
    
    public int getPolitics() {
        return this.politics;
    }
    
    public void setPolitics(final int politics) {
        this.politics = politics;
    }
}
