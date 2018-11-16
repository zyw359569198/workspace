package com.reign.kfwd.dto;

public class KfwdClient
{
    String gameServer;
    int seasonId;
    
    public KfwdClient(final String gameServer, final int seasonId) {
        this.gameServer = gameServer;
        this.seasonId = seasonId;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
}
