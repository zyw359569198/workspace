package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;

public class KfzbGameServerLimit implements IModel
{
    String gameServer;
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
}
