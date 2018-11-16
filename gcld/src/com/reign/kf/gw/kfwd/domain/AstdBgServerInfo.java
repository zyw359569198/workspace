package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class AstdBgServerInfo implements IModel
{
    private String gameServer;
    private int type;
    private String serverInfo;
    private long serverStartstamp;
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public String getServerInfo() {
        return this.serverInfo;
    }
    
    public void setServerInfo(final String serverInfo) {
        this.serverInfo = serverInfo;
    }
    
    public long getServerStartstamp() {
        return this.serverStartstamp;
    }
    
    public void setServerStartstamp(final long serverStartstamp) {
        this.serverStartstamp = serverStartstamp;
    }
}
