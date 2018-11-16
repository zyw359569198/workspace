package com.reign.kfzb.dto.response;

public class KfzbTreasureReward
{
    int pk;
    int pos;
    int treasureId;
    int lea;
    int str;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public int getTreasureId() {
        return this.treasureId;
    }
    
    public void setTreasureId(final int treasureId) {
        this.treasureId = treasureId;
    }
    
    public int getLea() {
        return this.lea;
    }
    
    public void setLea(final int lea) {
        this.lea = lea;
    }
    
    public int getStr() {
        return this.str;
    }
    
    public void setStr(final int str) {
        this.str = str;
    }
}
