package com.reign.kf.comm.entity.kfwd.response;

public class PTopPlayerInfo
{
    private Integer competitorId;
    private Integer seasonId;
    private Integer playerId;
    private String playerName;
    private Integer playerLevel;
    private String gameServer;
    private String serverName;
    private String serverPinyin;
    private String serverId;
    private int nation;
    private String officeName;
    private int winNum;
    private int score;
    private int pos;
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
    
    public Integer getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final Integer competitorId) {
        this.competitorId = competitorId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public Integer getPlayerLevel() {
        return this.playerLevel;
    }
    
    public void setPlayerLevel(final Integer playerLevel) {
        this.playerLevel = playerLevel;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerPinyin() {
        return this.serverPinyin;
    }
    
    public void setServerPinyin(final String serverPinyin) {
        this.serverPinyin = serverPinyin;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public String getOfficeName() {
        return this.officeName;
    }
    
    public void setOfficeName(final String officeName) {
        this.officeName = officeName;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
}
