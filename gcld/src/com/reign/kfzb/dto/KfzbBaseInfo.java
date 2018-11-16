package com.reign.kfzb.dto;

public class KfzbBaseInfo
{
    int kfzbState;
    long nextStateCD;
    
    public int getKfzbState() {
        return this.kfzbState;
    }
    
    public void setKfzbState(final int kfzbState) {
        this.kfzbState = kfzbState;
    }
    
    public long getNextStateCD() {
        return this.nextStateCD;
    }
    
    public void setNextStateCD(final long nextStateCD) {
        this.nextStateCD = nextStateCD;
    }
}
