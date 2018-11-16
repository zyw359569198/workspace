package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerKillInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer forceId;
    private Integer killNum;
    private Date killDate;
    private String box_reward_info;
    
    public String getBox_reward_info() {
        return this.box_reward_info;
    }
    
    public void setBox_reward_info(final String box_reward_info) {
        this.box_reward_info = box_reward_info;
    }
    
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
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final Integer killNum) {
        this.killNum = killNum;
    }
    
    public Date getKillDate() {
        return this.killDate;
    }
    
    public void setKillDate(final Date killDate) {
        this.killDate = killDate;
    }
}
