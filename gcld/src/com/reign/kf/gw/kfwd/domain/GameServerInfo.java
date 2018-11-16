package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class GameServerInfo implements IModel
{
    private int pk;
    private String gameServer;
    private int type;
    private Date lastSynDate;
    private String serverInfo;
    private String serverName;
    private int gzLayerId1;
    private int gzLayerId2;
    private int gzLayerId3;
    
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
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public Date getLastSynDate() {
        return this.lastSynDate;
    }
    
    public void setLastSynDate(final Date lastSynDate) {
        this.lastSynDate = lastSynDate;
    }
    
    public String getServerInfo() {
        return this.serverInfo;
    }
    
    public void setServerInfo(final String serverInfo) {
        this.serverInfo = serverInfo;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public int getGzLayerId1() {
        return this.gzLayerId1;
    }
    
    public void setGzLayerId1(final int gzLayerId1) {
        this.gzLayerId1 = gzLayerId1;
    }
    
    public int getGzLayerId2() {
        return this.gzLayerId2;
    }
    
    public void setGzLayerId2(final int gzLayerId2) {
        this.gzLayerId2 = gzLayerId2;
    }
    
    public int getGzLayerId3() {
        return this.gzLayerId3;
    }
    
    public void setGzLayerId3(final int gzLayerId3) {
        this.gzLayerId3 = gzLayerId3;
    }
    
    public int getGzLayerIdByNation(final int nation) {
        if (nation == 1) {
            return this.gzLayerId1;
        }
        if (nation == 2) {
            return this.gzLayerId2;
        }
        if (nation == 3) {
            return this.gzLayerId3;
        }
        return -1;
    }
}
