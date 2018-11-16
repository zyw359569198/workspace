package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzPlayerRankingInfo implements IModel
{
    int pk;
    int seasonId;
    int layerId;
    int gzId;
    int gId;
    int round;
    int cId;
    String playerName;
    int playerLv;
    String gameServer;
    int nation;
    String serverName;
    String serverId;
    long killArmy;
    int soloNum;
    int occupyCity;
    String gInfos;
    
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
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
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
    
    public long getKillArmy() {
        return this.killArmy;
    }
    
    public void setKillArmy(final long killArmy) {
        this.killArmy = killArmy;
    }
    
    public int getSoloNum() {
        return this.soloNum;
    }
    
    public void setSoloNum(final int soloNum) {
        this.soloNum = soloNum;
    }
    
    public int getOccupyCity() {
        return this.occupyCity;
    }
    
    public void setOccupyCity(final int occupyCity) {
        this.occupyCity = occupyCity;
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
    
    public String getgInfos() {
        return this.gInfos;
    }
    
    public void setgInfos(final String gInfos) {
        this.gInfos = gInfos;
    }
}
