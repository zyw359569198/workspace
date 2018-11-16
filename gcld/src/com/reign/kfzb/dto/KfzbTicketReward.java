package com.reign.kfzb.dto;

import java.util.*;

public class KfzbTicketReward
{
    String gameServer;
    int cId;
    boolean isFinish;
    int totalLayer;
    List<Integer> rewardTicketList;
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public boolean isFinish() {
        return this.isFinish;
    }
    
    public void setFinish(final boolean isFinish) {
        this.isFinish = isFinish;
    }
    
    public List<Integer> getRewardTicketList() {
        return this.rewardTicketList;
    }
    
    public void setRewardTicketList(final List<Integer> rewardTicketList) {
        this.rewardTicketList = rewardTicketList;
    }
    
    public int getTotalLayer() {
        return this.totalLayer;
    }
    
    public void setTotalLayer(final int totalLayer) {
        this.totalLayer = totalLayer;
    }
}
