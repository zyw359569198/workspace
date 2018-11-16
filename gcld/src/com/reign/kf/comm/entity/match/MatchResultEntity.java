package com.reign.kf.comm.entity.match;

public class MatchResultEntity
{
    private int competitorId;
    private int winNum;
    private int failNum;
    private int rank;
    private int score;
    private int totalScore;
    private String playerName;
    private String serverId;
    private String serverName;
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
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
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getTotalScore() {
        return this.totalScore;
    }
    
    public void setTotalScore(final int totalScore) {
        this.totalScore = totalScore;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
}
