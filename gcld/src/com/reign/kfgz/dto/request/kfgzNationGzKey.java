package com.reign.kfgz.dto.request;

public class kfgzNationGzKey
{
    String gameServer;
    int nation;
    int gzId;
    boolean ini;
    int seasonId;
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public boolean isIni() {
        return this.ini;
    }
    
    public void setIni(final boolean ini) {
        this.ini = ini;
    }
}
