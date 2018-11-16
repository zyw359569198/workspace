package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;

public class PlayerTaobao implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
}
