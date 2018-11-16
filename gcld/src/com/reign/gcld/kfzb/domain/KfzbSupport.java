package com.reign.gcld.kfzb.domain;

import com.reign.framework.mybatis.*;

public class KfzbSupport implements IModel
{
    public static final int TAKEID_TRUE = 1;
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer seasonId;
    private Integer matchId;
    private Integer roundId;
    private Integer cId;
    private Integer tickets;
    private Integer rewarded;
    private Integer takeIt;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final Integer matchId) {
        this.matchId = matchId;
    }
    
    public Integer getRoundId() {
        return this.roundId;
    }
    
    public void setRoundId(final Integer roundId) {
        this.roundId = roundId;
    }
    
    public Integer getCId() {
        return this.cId;
    }
    
    public void setCId(final Integer cId) {
        this.cId = cId;
    }
    
    public Integer getTickets() {
        return this.tickets;
    }
    
    public void setTickets(final Integer tickets) {
        this.tickets = tickets;
    }
    
    public Integer getRewarded() {
        return this.rewarded;
    }
    
    public void setRewarded(final Integer rewarded) {
        this.rewarded = rewarded;
    }
    
    public Integer getTakeIt() {
        return this.takeIt;
    }
    
    public void setTakeIt(final Integer takeIt) {
        this.takeIt = takeIt;
    }
}
