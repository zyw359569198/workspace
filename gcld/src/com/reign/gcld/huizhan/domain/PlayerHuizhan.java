package com.reign.gcld.huizhan.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerHuizhan implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer hzId;
    private Integer playerId;
    private Integer phantomNum;
    private Integer pkTimes;
    private Integer killNum;
    private Integer forces;
    private Integer forceId;
    private Integer cityId;
    private Integer awardFlag;
    private Date joinTime;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getHzId() {
        return this.hzId;
    }
    
    public void setHzId(final Integer hzId) {
        this.hzId = hzId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPhantomNum() {
        return this.phantomNum;
    }
    
    public void setPhantomNum(final Integer phantomNum) {
        this.phantomNum = phantomNum;
    }
    
    public Integer getPkTimes() {
        return this.pkTimes;
    }
    
    public void setPkTimes(final Integer pkTimes) {
        this.pkTimes = pkTimes;
    }
    
    public Integer getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final Integer killNum) {
        this.killNum = killNum;
    }
    
    public Integer getForces() {
        return this.forces;
    }
    
    public void setForces(final Integer forces) {
        this.forces = forces;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Integer getAwardFlag() {
        return this.awardFlag;
    }
    
    public void setAwardFlag(final Integer awardFlag) {
        this.awardFlag = awardFlag;
    }
    
    public Date getJoinTime() {
        return this.joinTime;
    }
    
    public void setJoinTime(final Date joinTime) {
        this.joinTime = joinTime;
    }
}
