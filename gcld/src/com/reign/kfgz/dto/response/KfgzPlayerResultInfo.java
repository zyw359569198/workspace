package com.reign.kfgz.dto.response;

public class KfgzPlayerResultInfo
{
    int cId;
    long killArmy;
    int killRank;
    int soloWinNum;
    int occupyCity;
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public long getKillArmy() {
        return this.killArmy;
    }
    
    public void setKillArmy(final long killArmy) {
        this.killArmy = killArmy;
    }
    
    public int getKillRank() {
        return this.killRank;
    }
    
    public void setKillRank(final int killRank) {
        this.killRank = killRank;
    }
    
    public int getSoloWinNum() {
        return this.soloWinNum;
    }
    
    public void setSoloWinNum(final int soloWinNum) {
        this.soloWinNum = soloWinNum;
    }
    
    public int getOccupyCity() {
        return this.occupyCity;
    }
    
    public void setOccupyCity(final int occupyCity) {
        this.occupyCity = occupyCity;
    }
}
