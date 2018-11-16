package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class KfwdRankTreasure implements IModel
{
    int pk;
    int gid;
    int tid;
    int strMax;
    int strMin;
    int leaMax;
    int leaMin;
    int minRanking;
    int maxRanking;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getGid() {
        return this.gid;
    }
    
    public void setGid(final int gid) {
        this.gid = gid;
    }
    
    public int getTid() {
        return this.tid;
    }
    
    public void setTid(final int tid) {
        this.tid = tid;
    }
    
    public int getStrMax() {
        return this.strMax;
    }
    
    public void setStrMax(final int strMax) {
        this.strMax = strMax;
    }
    
    public int getStrMin() {
        return this.strMin;
    }
    
    public void setStrMin(final int strMin) {
        this.strMin = strMin;
    }
    
    public int getLeaMax() {
        return this.leaMax;
    }
    
    public void setLeaMax(final int leaMax) {
        this.leaMax = leaMax;
    }
    
    public int getLeaMin() {
        return this.leaMin;
    }
    
    public void setLeaMin(final int leaMin) {
        this.leaMin = leaMin;
    }
    
    public int getMinRanking() {
        return this.minRanking;
    }
    
    public void setMinRanking(final int minRanking) {
        this.minRanking = minRanking;
    }
    
    public int getMaxRanking() {
        return this.maxRanking;
    }
    
    public void setMaxRanking(final int maxRanking) {
        this.maxRanking = maxRanking;
    }
}
