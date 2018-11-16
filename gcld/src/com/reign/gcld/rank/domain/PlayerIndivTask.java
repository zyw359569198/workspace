package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class PlayerIndivTask implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String indivTaskInfo;
    private Integer forceId;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getIndivTaskInfo() {
        return this.indivTaskInfo;
    }
    
    public void setIndivTaskInfo(final String indivTaskInfo) {
        this.indivTaskInfo = indivTaskInfo;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
}
