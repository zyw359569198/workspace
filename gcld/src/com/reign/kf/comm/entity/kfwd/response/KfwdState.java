package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;

public class KfwdState implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    private long currentTimestamp;
    private int seasonId;
    private int globalState;
    private long nextGlobalStateCD;
    
    public long getNextGlobalStateCD() {
        return this.nextGlobalStateCD;
    }
    
    public void setNextGlobalStateCD(final long nextGlobalStateCD) {
        this.nextGlobalStateCD = nextGlobalStateCD;
    }
    
    public long getCurrentTimestamp() {
        return this.currentTimestamp;
    }
    
    public void setCurrentTimestamp(final long currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getGlobalState() {
        return this.globalState;
    }
    
    public void setGlobalState(final int globalState) {
        this.globalState = globalState;
    }
}
