package com.reign.kfgz.dto;

public class KfgzNpcAIChoosenInfo
{
    int gzId;
    int forceId;
    long lastSetTime;
    int chooseResult;
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public long getLastSetTime() {
        return this.lastSetTime;
    }
    
    public void setLastSetTime(final long lastSetTime) {
        this.lastSetTime = lastSetTime;
    }
    
    public int getChooseResult() {
        return this.chooseResult;
    }
    
    public void setChooseResult(final int chooseResult) {
        this.chooseResult = chooseResult;
    }
}
