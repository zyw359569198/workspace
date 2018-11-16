package com.reign.gcld.chat.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class Silence implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer silenceId;
    private Integer playerId;
    private Date nextSayTime;
    private String reason;
    private Date silenceTime;
    private String userId;
    private String yx;
    private Integer type;
    
    public Integer getSilenceId() {
        return this.silenceId;
    }
    
    public void setSilenceId(final Integer silenceId) {
        this.silenceId = silenceId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Date getNextSayTime() {
        return this.nextSayTime;
    }
    
    public void setNextSayTime(final Date nextSayTime) {
        this.nextSayTime = nextSayTime;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public Date getSilenceTime() {
        return this.silenceTime;
    }
    
    public void setSilenceTime(final Date silenceTime) {
        this.silenceTime = silenceTime;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
}
