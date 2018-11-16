package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzNationResult implements IModel
{
    int pk;
    int seasonId;
    int layerId;
    int gzId;
    int gId;
    int round;
    String gameServer;
    int nation;
    String serverName;
    int selfCity;
    int oppCity;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getLayerId() {
        return this.layerId;
    }
    
    public void setLayerId(final int layerId) {
        this.layerId = layerId;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public int getgId() {
        return this.gId;
    }
    
    public void setgId(final int gId) {
        this.gId = gId;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
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
    
    public int getSelfCity() {
        return this.selfCity;
    }
    
    public void setSelfCity(final int selfCity) {
        this.selfCity = selfCity;
    }
    
    public int getOppCity() {
        return this.oppCity;
    }
    
    public void setOppCity(final int oppCity) {
        this.oppCity = oppCity;
    }
}
