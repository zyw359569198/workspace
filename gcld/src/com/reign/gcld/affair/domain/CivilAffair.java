package com.reign.gcld.affair.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class CivilAffair implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer generalId;
    private Integer affairId;
    private Date startTime;
    private Integer lv;
    private Integer upgradeShow;
    
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
    
    public Integer getAffairId() {
        return this.affairId;
    }
    
    public void setAffairId(final Integer affairId) {
        this.affairId = affairId;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getUpgradeShow() {
        return this.upgradeShow;
    }
    
    public void setUpgradeShow(final Integer upgradeShow) {
        this.upgradeShow = upgradeShow;
    }
}
