package com.reign.kf.comm.entity.match;

public class MatchRankEntity
{
    private int competitorId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private String forceName;
    private int playerPic;
    private String serverName;
    private String serverId;
    private String legionName;
    private int winNum;
    private int failNum;
    private int rank;
    private int turn;
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
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
    
    public String getForceName() {
        return this.forceName;
    }
    
    public void setForceName(final String forceName) {
        this.forceName = forceName;
    }
    
    public int getPlayerPic() {
        return this.playerPic;
    }
    
    public void setPlayerPic(final int playerPic) {
        this.playerPic = playerPic;
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
    
    public String getLegionName() {
        return this.legionName;
    }
    
    public void setLegionName(final String legionName) {
        this.legionName = legionName;
    }
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
    
    public int getFailNum() {
        return this.failNum;
    }
    
    public void setFailNum(final int failNum) {
        this.failNum = failNum;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public void setTurn(final int turn) {
        this.turn = turn;
    }
}
