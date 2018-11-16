package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class PlayerExpandInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer killnum;
    private Integer rank;
    private Integer isrewarded;
    private Integer forceid;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getKillnum() {
        return this.killnum;
    }
    
    public void setKillnum(final Integer killnum) {
        this.killnum = killnum;
    }
    
    public Integer getRank() {
        return this.rank;
    }
    
    public void setRank(final Integer rank) {
        this.rank = rank;
    }
    
    public Integer getIsrewarded() {
        return this.isrewarded;
    }
    
    public void setIsrewarded(final Integer isrewarded) {
        this.isrewarded = isrewarded;
    }
    
    public Integer getForceid() {
        return this.forceid;
    }
    
    public void setForceid(final Integer forceid) {
        this.forceid = forceid;
    }
}
