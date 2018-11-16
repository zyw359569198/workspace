package com.reign.gcld.feat.domain;

import com.reign.framework.mybatis.*;

public class PlayerFeatRank implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer killNum;
    private Integer occupyNum;
    private Integer assistNum;
    private Integer cheerNum;
    private Integer killFeat;
    private Integer totalFeat;
    private Integer lastRank;
    private Integer received;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final Integer killNum) {
        this.killNum = killNum;
    }
    
    public Integer getOccupyNum() {
        return this.occupyNum;
    }
    
    public void setOccupyNum(final Integer occupyNum) {
        this.occupyNum = occupyNum;
    }
    
    public Integer getAssistNum() {
        return this.assistNum;
    }
    
    public void setAssistNum(final Integer assistNum) {
        this.assistNum = assistNum;
    }
    
    public Integer getCheerNum() {
        return this.cheerNum;
    }
    
    public void setCheerNum(final Integer cheerNum) {
        this.cheerNum = cheerNum;
    }
    
    public Integer getKillFeat() {
        return this.killFeat;
    }
    
    public void setKillFeat(final Integer killFeat) {
        this.killFeat = killFeat;
    }
    
    public Integer getTotalFeat() {
        return this.totalFeat;
    }
    
    public void setTotalFeat(final Integer totalFeat) {
        this.totalFeat = totalFeat;
    }
    
    public Integer getLastRank() {
        return this.lastRank;
    }
    
    public void setLastRank(final Integer lastRank) {
        this.lastRank = lastRank;
    }
    
    public Integer getReceived() {
        return this.received;
    }
    
    public void setReceived(final Integer received) {
        this.received = received;
    }
}
