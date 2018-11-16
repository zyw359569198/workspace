package com.reign.gcld.store.domain;

import com.reign.framework.mybatis.*;

public class PlayerQuenchingRelative implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer freeQuenchingTimes;
    private Integer freeNiubiQuenchingTimes;
    private Integer remind;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getFreeQuenchingTimes() {
        return this.freeQuenchingTimes;
    }
    
    public void setFreeQuenchingTimes(final Integer freeQuenchingTimes) {
        this.freeQuenchingTimes = freeQuenchingTimes;
    }
    
    public Integer getFreeNiubiQuenchingTimes() {
        return this.freeNiubiQuenchingTimes;
    }
    
    public void setFreeNiubiQuenchingTimes(final Integer freeNiubiQuenchingTimes) {
        this.freeNiubiQuenchingTimes = freeNiubiQuenchingTimes;
    }
    
    public Integer getRemind() {
        return this.remind;
    }
    
    public void setRemind(final Integer remind) {
        this.remind = remind;
    }
}
