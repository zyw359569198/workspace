package com.reign.kfzb.dto.response;

import java.util.*;

public class FrameBattleReport
{
    public static final int STATE_REWARD_HALF = 1;
    public static final int STATE_REWARD_NONE = 2;
    int frame;
    boolean isEnd;
    String iniReport;
    String battleReport;
    Date nextRoundTime;
    int state;
    
    public int getFrame() {
        return this.frame;
    }
    
    public void setFrame(final int frame) {
        this.frame = frame;
    }
    
    public String getIniReport() {
        return this.iniReport;
    }
    
    public void setIniReport(final String iniReport) {
        this.iniReport = iniReport;
    }
    
    public String getBattleReport() {
        return this.battleReport;
    }
    
    public void setBattleReport(final String battleReport) {
        this.battleReport = battleReport;
    }
    
    public boolean isEnd() {
        return this.isEnd;
    }
    
    public void setEnd(final boolean isEnd) {
        this.isEnd = isEnd;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public Date getNextRoundTime() {
        return this.nextRoundTime;
    }
    
    public void setNextRoundTime(final Date nextRoundTime) {
        this.nextRoundTime = nextRoundTime;
    }
}
