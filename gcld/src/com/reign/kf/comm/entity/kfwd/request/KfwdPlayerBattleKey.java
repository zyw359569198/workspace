package com.reign.kf.comm.entity.kfwd.request;

import java.io.*;

public class KfwdPlayerBattleKey implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int competitorId;
    private int round;
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
}
