package com.reign.gcld.juben.domain;

import com.reign.framework.mybatis.*;

public class ScenarioInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer soloId;
    private Integer grade;
    private Integer minTime;
    private Integer playerId;
    private Integer forceId;
    private String playerName;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getSoloId() {
        return this.soloId;
    }
    
    public void setSoloId(final Integer soloId) {
        this.soloId = soloId;
    }
    
    public Integer getGrade() {
        return this.grade;
    }
    
    public void setGrade(final Integer grade) {
        this.grade = grade;
    }
    
    public Integer getMinTime() {
        return this.minTime;
    }
    
    public void setMinTime(final Integer minTime) {
        this.minTime = minTime;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
}
