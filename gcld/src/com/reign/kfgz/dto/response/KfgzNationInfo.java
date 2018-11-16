package com.reign.kfgz.dto.response;

public class KfgzNationInfo
{
    String gameServer;
    int layerId;
    int nation;
    String serverName;
    
    public KfgzNationInfo() {
    }
    
    public KfgzNationInfo(final String gameServer2, final String serverName2, final int nation, final int gzLayerId1) {
        this.gameServer = gameServer2;
        this.serverName = serverName2;
        this.layerId = gzLayerId1;
        this.nation = nation;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getLayerId() {
        return this.layerId;
    }
    
    public void setLayerId(final int layerId) {
        this.layerId = layerId;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
}
