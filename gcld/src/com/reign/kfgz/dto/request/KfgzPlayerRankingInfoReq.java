package com.reign.kfgz.dto.request;

public class KfgzPlayerRankingInfoReq
{
    int seasonId;
    int layerId;
    int gzId;
    int gId;
    int round;
    int cId;
    int nation;
    String playerName;
    int playerLv;
    String serverName;
    String serverId;
    String gameServer;
    long killArmy;
    int soloNum;
    int occupyCity;
    String gInfos;
    int pos;
    
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
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public String getgInfos() {
        return this.gInfos;
    }
    
    public void setgInfos(final String gInfos) {
        this.gInfos = gInfos;
    }
}
