package com.reign.kf.comm.entity.kfwd.request;

public class KfwdRTInspire
{
    private int competitorId;
    private int round;
    private int attNum;
    private int defNum;
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
    
    public int getAttNum() {
        return this.attNum;
    }
    
    public void setAttNum(final int attNum) {
        this.attNum = attNum;
    }
    
    public int getDefNum() {
        return this.defNum;
    }
    
    public void setDefNum(final int defNum) {
        this.defNum = defNum;
    }
}
