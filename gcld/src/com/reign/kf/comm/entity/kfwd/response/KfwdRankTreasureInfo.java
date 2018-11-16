package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfwdRankTreasureInfo
{
    public static Random ran;
    int pk;
    int gid;
    int tid;
    int strMax;
    int strMin;
    int leaMax;
    int leaMin;
    int minRanking;
    int maxRanking;
    
    static {
        KfwdRankTreasureInfo.ran = new Random();
    }
    
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
    
    @JsonIgnore
    public int[] getRandomLeaAndStr() {
        final int strRange = this.strMax - this.strMin;
        int strAdd = 0;
        if (strRange > 0) {
            strAdd = KfwdRankTreasureInfo.ran.nextInt(strRange);
        }
        final int leaRange = this.leaMax - this.leaMin;
        int leaAdd = 0;
        if (leaRange > 0) {
            leaAdd = KfwdRankTreasureInfo.ran.nextInt(leaRange);
        }
        return new int[] { this.leaMin + leaAdd, this.strMin + strAdd };
    }
}
