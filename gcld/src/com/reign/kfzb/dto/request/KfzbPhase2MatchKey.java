package com.reign.kfzb.dto.request;

public class KfzbPhase2MatchKey
{
    int matchId;
    int roundId;
    int frame;
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getRoundId() {
        return this.roundId;
    }
    
    public void setRoundId(final int roundId) {
        this.roundId = roundId;
    }
    
    public int getFrame() {
        return this.frame;
    }
    
    public void setFrame(final int frame) {
        this.frame = frame;
    }
}
