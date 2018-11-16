package com.reign.kf.comm.entity;

public class GameServerEntity
{
    protected String serverKey;
    protected String serverName;
    
    public String getServerKey() {
        return this.serverKey;
    }
    
    public void setServerKey(final String serverKey) {
        this.serverKey = serverKey;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
}
