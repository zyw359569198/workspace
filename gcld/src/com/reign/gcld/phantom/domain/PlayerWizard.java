package com.reign.gcld.phantom.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerWizard implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer wizardId;
    private Integer level;
    private Integer flag;
    private Integer todayNum;
    private Integer num;
    private Date succTime;
    private String reserve;
    private Integer extraPicked;
    
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
    
    public Integer getWizardId() {
        return this.wizardId;
    }
    
    public void setWizardId(final Integer wizardId) {
        this.wizardId = wizardId;
    }
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public Integer getFlag() {
        return this.flag;
    }
    
    public void setFlag(final Integer flag) {
        this.flag = flag;
    }
    
    public Integer getTodayNum() {
        return this.todayNum;
    }
    
    public void setTodayNum(final Integer todayNum) {
        this.todayNum = todayNum;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Date getSuccTime() {
        return this.succTime;
    }
    
    public void setSuccTime(final Date succTime) {
        this.succTime = succTime;
    }
    
    public String getReserve() {
        return this.reserve;
    }
    
    public void setReserve(final String reserve) {
        this.reserve = reserve;
    }
    
    public Integer getExtraPicked() {
        return this.extraPicked;
    }
    
    public void setExtraPicked(final Integer extraPicked) {
        this.extraPicked = extraPicked;
    }
}
