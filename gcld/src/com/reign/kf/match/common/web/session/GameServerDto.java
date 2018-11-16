package com.reign.kf.match.common.web.session;

public class GameServerDto extends ConnectorDto
{
    private String serverName;
    private int serverId;
    private String logStr;
    
    public GameServerDto(final String serverName, final int serverId) {
        this.type = 1;
        this.serverName = serverName;
        this.serverId = serverId;
        this.logStr = String.valueOf(serverName) + "#" + serverId;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public int getServerId() {
        return this.serverId;
    }
    
    @Override
    public String buildLogStr() {
        return this.logStr;
    }
}
