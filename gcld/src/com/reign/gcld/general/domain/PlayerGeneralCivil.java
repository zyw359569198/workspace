package com.reign.gcld.general.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerGeneralCivil implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer generalId;
    private Integer lv;
    private Long exp;
    private Integer intel;
    private Integer politics;
    private Date updateTime;
    private Date nextMoveTime;
    private Integer state;
    private Integer taskId;
    private Integer owner;
    private Date cd;
    
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
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Long getExp() {
        return this.exp;
    }
    
    public void setExp(final Long exp) {
        this.exp = exp;
    }
    
    public Integer getIntel() {
        return this.intel;
    }
    
    public Integer getIntel(final Integer addInteger) {
        return this.intel + ((addInteger == null) ? 0 : addInteger);
    }
    
    public void setIntel(final Integer intel) {
        this.intel = intel;
    }
    
    public Integer getPolitics() {
        return this.politics;
    }
    
    public Integer getPolitics(final Integer addInteger) {
        return this.politics + ((addInteger == null) ? 0 : addInteger);
    }
    
    public void setPolitics(final Integer politics) {
        this.politics = politics;
    }
    
    public Date getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Date getNextMoveTime() {
        return this.nextMoveTime;
    }
    
    public void setNextMoveTime(final Date nextMoveTime) {
        this.nextMoveTime = nextMoveTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }
    
    public Integer getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Integer owner) {
        this.owner = owner;
    }
    
    public Date getCd() {
        return this.cd;
    }
    
    public void setCd(final Date cd) {
        this.cd = cd;
    }
}
