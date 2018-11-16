package com.reign.kfzb.dto;

public class KfzbClient
{
    String gameServer;
    int seasonId;
    
    public KfzbClient(final String gameServer, final int seasonId) {
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
