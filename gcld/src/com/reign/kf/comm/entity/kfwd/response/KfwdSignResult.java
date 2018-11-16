package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;

public class KfwdSignResult implements Serializable
{
    private int state;
    private Integer playerId;
    private Integer competitor;
    
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
    
    public Integer getCompetitor() {
        return this.competitor;
    }
    
    public void setCompetitor(final Integer competitor) {
        this.competitor = competitor;
    }
}
