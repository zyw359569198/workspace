package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;

public class PlayerName implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private String playerName;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
}
