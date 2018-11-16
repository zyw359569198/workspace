package com.reign.gcld.pay.domain;

import com.reign.framework.mybatis.*;

public class PlayerVip implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private String vipStatus;
    private String vipRemainingTimes;
    
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
    
    public String getVipStatus() {
        return this.vipStatus;
    }
    
    public void setVipStatus(final String vipStatus) {
        this.vipStatus = vipStatus;
    }
    
    public String getVipRemainingTimes() {
        return this.vipRemainingTimes;
    }
    
    public void setVipRemainingTimes(final String vipRemainingTimes) {
        this.vipRemainingTimes = vipRemainingTimes;
    }
}
