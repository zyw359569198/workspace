package com.reign.gcld.kfwd.dto;

public class KfwdBaseInfo
{
    int kfwdState;
    long nextStateCD;
    
    public int getKfwdState() {
        return this.kfwdState;
    }
    
    public void setKfwdState(final int kfwdState) {
        this.kfwdState = kfwdState;
    }
    
    public long getNextStateCD() {
        return this.nextStateCD;
    }
    
    public void setNextStateCD(final long nextStateCD) {
        this.nextStateCD = nextStateCD;
    }
}
