package com.reign.kfgz.dto;

public class KfgzRankingDto
{
    int cId;
    long value;
    int pos;
    long upNeedScore;
    
    public KfgzRankingDto(final int cId, final int num) {
        this.cId = cId;
        this.value = num;
    }
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public long getValue() {
        return this.value;
    }
    
    public void setValue(final long value) {
        this.value = value;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public long getUpNeedScore() {
        return this.upNeedScore;
    }
    
    public void setUpNeedScore(final long upNeedScore) {
        this.upNeedScore = upNeedScore;
    }
    
    public long getScore() {
        return this.value;
    }
    
    public int getRank() {
        return this.pos;
    }
    
    public void setScore(final long value) {
        this.value = value;
    }
    
    public void setRank(final int i) {
        this.pos = i;
    }
}
