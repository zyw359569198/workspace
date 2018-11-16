package com.reign.gcld.user.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class UserBlock implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private String userId;
    private String yx;
    private Integer playerId;
    private String reason;
    private Date blockEndTime;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
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
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public Date getBlockEndTime() {
        return this.blockEndTime;
    }
    
    public void setBlockEndTime(final Date blockEndTime) {
        this.blockEndTime = blockEndTime;
    }
}
