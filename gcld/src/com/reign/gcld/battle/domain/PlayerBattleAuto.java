package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class PlayerBattleAuto implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer defId;
    private Integer mode;
    private Integer times;
    private String report;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getDefId() {
        return this.defId;
    }
    
    public void setDefId(final Integer defId) {
        this.defId = defId;
    }
    
    public Integer getMode() {
        return this.mode;
    }
    
    public void setMode(final Integer mode) {
        this.mode = mode;
    }
    
    public Integer getTimes() {
        return this.times;
    }
    
    public void setTimes(final Integer times) {
        this.times = times;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
}
