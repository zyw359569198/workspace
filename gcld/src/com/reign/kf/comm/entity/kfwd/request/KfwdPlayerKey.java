package com.reign.kf.comm.entity.kfwd.request;

import java.io.*;

public class KfwdPlayerKey implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int competitorId;
    private int scheduleId;
    private int playerId;
    
    public KfwdPlayerKey(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
}
