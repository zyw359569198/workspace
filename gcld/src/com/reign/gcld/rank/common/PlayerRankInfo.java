package com.reign.gcld.rank.common;

public class PlayerRankInfo
{
    private int playerId;
    private MultiRankData rankData;
    private int rank;
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public MultiRankData getRankData() {
        return this.rankData;
    }
    
    public void setRankData(final MultiRankData rankData) {
        this.rankData = rankData;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
}
