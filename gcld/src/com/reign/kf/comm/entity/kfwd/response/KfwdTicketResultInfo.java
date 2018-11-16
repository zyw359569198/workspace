package com.reign.kf.comm.entity.kfwd.response;

public class KfwdTicketResultInfo
{
    private int competitorId;
    private int scheduleId;
    private int seasonId;
    private int playerId;
    private String gameServer;
    private String rewardInfo;
    String dayRanking;
    int dayReward;
    String dayTicket;
    long winRes;
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public String getRewardInfo() {
        return this.rewardInfo;
    }
    
    public void setRewardInfo(final String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public String getDayRanking() {
        return this.dayRanking;
    }
    
    public void setDayRanking(final String dayRanking) {
        this.dayRanking = dayRanking;
    }
    
    public int getDayReward() {
        return this.dayReward;
    }
    
    public void setDayReward(final int dayReward) {
        this.dayReward = dayReward;
    }
    
    public String getDayTicket() {
        return this.dayTicket;
    }
    
    public void setDayTicket(final String dayTicket) {
        this.dayTicket = dayTicket;
    }
    
    public long getWinRes() {
        return this.winRes;
    }
    
    public void setWinRes(final long winRes) {
        this.winRes = winRes;
    }
}
