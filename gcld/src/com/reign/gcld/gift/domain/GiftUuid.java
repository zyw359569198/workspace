package com.reign.gcld.gift.domain;

import com.reign.framework.mybatis.*;

public class GiftUuid implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer uuid;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getUuid() {
        return this.uuid;
    }
    
    public void setUuid(final Integer uuid) {
        this.uuid = uuid;
    }
}
