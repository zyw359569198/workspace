package com.reign.gcld.activity.domain;

import com.reign.framework.mybatis.*;

public class PlayerDragon implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer dragonNum;
    private Integer boxNum;
    private Integer featBoxNum;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getDragonNum() {
        return this.dragonNum;
    }
    
    public void setDragonNum(final Integer dragonNum) {
        this.dragonNum = dragonNum;
    }
    
    public Integer getBoxNum() {
        return this.boxNum;
    }
    
    public void setBoxNum(final Integer boxNum) {
        this.boxNum = boxNum;
    }
    
    public Integer getFeatBoxNum() {
        return this.featBoxNum;
    }
    
    public void setFeatBoxNum(final Integer featBoxNum) {
        this.featBoxNum = featBoxNum;
    }
}
