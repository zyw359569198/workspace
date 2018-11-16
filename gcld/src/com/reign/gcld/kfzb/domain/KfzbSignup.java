package com.reign.gcld.kfzb.domain;

import com.reign.framework.mybatis.*;

public class KfzbSignup implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer seasonId;
    private Integer saiquId;
    private String info;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getSaiquId() {
        return this.saiquId;
    }
    
    public void setSaiquId(final Integer saiquId) {
        this.saiquId = saiquId;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
}
