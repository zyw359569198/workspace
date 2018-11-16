package com.reign.gcld.gift.domain;

import com.reign.framework.mybatis.*;

public class PlayerOnlineReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer remainOnlineNum;
    private Integer onlineNum;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getRemainOnlineNum() {
        return this.remainOnlineNum;
    }
    
    public void setRemainOnlineNum(final Integer remainOnlineNum) {
        this.remainOnlineNum = remainOnlineNum;
    }
    
    public Integer getOnlineNum() {
        return this.onlineNum;
    }
    
    public void setOnlineNum(final Integer onlineNum) {
        this.onlineNum = onlineNum;
    }
}
