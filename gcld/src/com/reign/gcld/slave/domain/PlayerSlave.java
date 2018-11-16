package com.reign.gcld.slave.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerSlave implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer slaveId;
    private Integer generalId;
    private Date grabTime;
    private Date cd;
    private Integer slashTimes;
    private Integer type;
    private Integer forceId;
    private String name;
    private Integer lv;
    
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
    
    public Integer getSlaveId() {
        return this.slaveId;
    }
    
    public void setSlaveId(final Integer slaveId) {
        this.slaveId = slaveId;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Date getGrabTime() {
        return this.grabTime;
    }
    
    public void setGrabTime(final Date grabTime) {
        this.grabTime = grabTime;
    }
    
    public Date getCd() {
        return this.cd;
    }
    
    public void setCd(final Date cd) {
        this.cd = cd;
    }
    
    public Integer getSlashTimes() {
        return this.slashTimes;
    }
    
    public void setSlashTimes(final Integer slashTimes) {
        this.slashTimes = slashTimes;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
}
