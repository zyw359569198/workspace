package com.reign.kf.comm.entity.kfwd.request;

public class KfwdPlayerMatchKey
{
    private int competitorId;
    private int round;
    private int sRound;
    private int scheduleId;
    
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
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getsRound() {
        return this.sRound;
    }
    
    public void setsRound(final int sRound) {
        this.sRound = sRound;
    }
}
