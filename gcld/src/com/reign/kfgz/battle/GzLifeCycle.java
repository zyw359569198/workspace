package com.reign.kfgz.battle;

import com.reign.kfgz.control.*;

public abstract class GzLifeCycle
{
    public int gzId;
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public boolean isStart() {
        return KfgzManager.isGzStartByGzId(this.gzId);
    }
    
    public boolean isEnd() {
        return KfgzManager.isGzEndByGzId(this.gzId);
    }
    
    public boolean canRun() {
        return this.isStart() && !this.isEnd();
    }
    
    public abstract void doEnd();
}
