package com.reign.gcld.player.domain;

public class PlayerTotalDay
{
    private Integer playerId;
    private Integer totalDay;
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setTotalDay(final Integer totalDay) {
        this.totalDay = totalDay;
    }
    
    public Integer getTotalDay() {
        return this.totalDay;
    }
}
