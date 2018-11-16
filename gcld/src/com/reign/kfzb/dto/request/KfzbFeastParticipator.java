package com.reign.kfzb.dto.request;

public class KfzbFeastParticipator
{
    int rank;
    int nation;
    int playerId;
    String playerName;
    String serverName;
    String serverId;
    
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
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
}
