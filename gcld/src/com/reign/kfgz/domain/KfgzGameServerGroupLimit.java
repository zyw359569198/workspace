package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzGameServerGroupLimit implements IModel
{
    int pk;
    String gameServer;
    int nation;
    int groupId;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final int groupId) {
        this.groupId = groupId;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
}
