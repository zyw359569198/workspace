package com.reign.gcld.chat.domain;

import com.reign.framework.mybatis.*;

public class PlayerBlack implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer blackId;
    private String playerName;
    
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
    
    public Integer getBlackId() {
        return this.blackId;
    }
    
    public void setBlackId(final Integer blackId) {
        this.blackId = blackId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
}
