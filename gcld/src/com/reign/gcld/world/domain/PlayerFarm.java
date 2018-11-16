package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerFarm implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer generalId;
    private Integer type;
    private Date endTime;
    private Integer reward;
    private Integer time;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public Integer getReward() {
        return this.reward;
    }
    
    public void setReward(final Integer reward) {
        this.reward = reward;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
}
