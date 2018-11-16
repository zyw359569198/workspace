package com.reign.gcld.nation.domain;

import com.reign.framework.mybatis.*;

public class PlayerTryRank implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer num;
    private Integer received;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getReceived() {
        return this.received;
    }
    
    public void setReceived(final Integer received) {
        this.received = received;
    }
}
