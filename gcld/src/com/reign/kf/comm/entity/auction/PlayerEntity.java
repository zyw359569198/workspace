package com.reign.kf.comm.entity.auction;

public class PlayerEntity
{
    private int auctionId;
    private String playerName;
    private String serverName;
    private String serverId;
    
    public int getAuctionId() {
        return this.auctionId;
    }
    
    public void setAuctionId(final int auctionId) {
        this.auctionId = auctionId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
}
