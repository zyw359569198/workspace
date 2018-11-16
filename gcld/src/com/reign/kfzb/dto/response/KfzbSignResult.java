package com.reign.kfzb.dto.response;

import java.io.*;

public class KfzbSignResult implements Serializable
{
    private int state;
    private Integer playerId;
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
}
