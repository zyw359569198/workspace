package com.reign.gcld.juben.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerScenario implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer scenarioId;
    private Integer attackable;
    private Integer grade;
    private Integer rewarded;
    private Integer state;
    private String eventInfo;
    private Date starttime;
    private Date endtime;
    private String starlv;
    private Integer curstar;
    private Integer jieBingCount;
    private Long overtime;
    private String dramaTimes;
    
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
    
    public Integer getScenarioId() {
        return this.scenarioId;
    }
    
    public void setScenarioId(final Integer scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    public Integer getAttackable() {
        return this.attackable;
    }
    
    public void setAttackable(final Integer attackable) {
        this.attackable = attackable;
    }
    
    public Integer getGrade() {
        return this.grade;
    }
    
    public void setGrade(final Integer grade) {
        this.grade = grade;
    }
    
    public Integer getRewarded() {
        return this.rewarded;
    }
    
    public void setRewarded(final Integer rewarded) {
        this.rewarded = rewarded;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public String getEventInfo() {
        return this.eventInfo;
    }
    
    public void setEventInfo(final String eventInfo) {
        this.eventInfo = eventInfo;
    }
    
    public Date getStarttime() {
        return this.starttime;
    }
    
    public void setStarttime(final Date starttime) {
        this.starttime = starttime;
    }
    
    public Date getEndtime() {
        return this.endtime;
    }
    
    public void setEndtime(final Date endtime) {
        this.endtime = endtime;
    }
    
    public String getStarlv() {
        return this.starlv;
    }
    
    public void setStarlv(final String starlv) {
        this.starlv = starlv;
    }
    
    public Integer getCurstar() {
        return this.curstar;
    }
    
    public void setCurstar(final Integer curstar) {
        this.curstar = curstar;
    }
    
    public Integer getJieBingCount() {
        return this.jieBingCount;
    }
    
    public void setJieBingCount(final Integer jieBingCount) {
        this.jieBingCount = jieBingCount;
    }
    
    public Long getOvertime() {
        return this.overtime;
    }
    
    public void setOvertime(final Long overtime) {
        this.overtime = overtime;
    }
    
    public String getDramaTimes() {
        return this.dramaTimes;
    }
    
    public void setDramaTimes(final String dramaTimes) {
        this.dramaTimes = dramaTimes;
    }
}
