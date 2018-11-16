package com.reign.gcld.kfwd.manager;

public class KfwdSignInfo
{
    int playerId;
    int completedId;
    int scheduleId;
    int seasonId;
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getCompletedId() {
        return this.completedId;
    }
    
    public void setCompletedId(final int completedId) {
        this.completedId = completedId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
}
