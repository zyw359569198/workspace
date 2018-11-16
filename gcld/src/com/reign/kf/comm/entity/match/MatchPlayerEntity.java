package com.reign.kf.comm.entity.match;

public class MatchPlayerEntity
{
    private int competitorId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private String forceName;
    private int forceId;
    private int playerPic;
    private String serverName;
    private String serverId;
    private byte[] campInfo;
    private int winNum;
    private int failNum;
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
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
    
    public byte[] getCampInfo() {
        return this.campInfo;
    }
    
    public void setCampInfo(final byte[] campInfo) {
        this.campInfo = campInfo;
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
}
