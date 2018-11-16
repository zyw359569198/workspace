package com.reign.gcld.juben.domain;

import com.reign.framework.mybatis.*;

public class PlayerScenarioCity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer scenarioId;
    private Integer cityId;
    private Integer forceId;
    private Integer state;
    private Integer title;
    private String trickinfo;
    private Integer border;
    private String eventInfo;
    private Long updatetime;
    
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
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getTitle() {
        return this.title;
    }
    
    public void setTitle(final Integer title) {
        this.title = title;
    }
    
    public String getTrickinfo() {
        return this.trickinfo;
    }
    
    public void setTrickinfo(final String trickinfo) {
        this.trickinfo = trickinfo;
    }
    
    public Integer getBorder() {
        return this.border;
    }
    
    public void setBorder(final Integer border) {
        this.border = border;
    }
    
    public String getEventInfo() {
        return this.eventInfo;
    }
    
    public void setEventInfo(final String eventInfo) {
        this.eventInfo = eventInfo;
    }
    
    public Long getUpdatetime() {
        return this.updatetime;
    }
    
    public void setUpdatetime(final Long updatetime) {
        this.updatetime = updatetime;
    }
}
