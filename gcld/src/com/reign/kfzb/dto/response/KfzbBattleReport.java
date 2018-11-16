package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbBattleReport
{
    int matchId;
    int round;
    List<FrameBattleReport> list;
    
    public KfzbBattleReport() {
        this.list = new ArrayList<FrameBattleReport>();
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public List<FrameBattleReport> getList() {
        return this.list;
    }
    
    public void setList(final List<FrameBattleReport> list) {
        this.list = list;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
}
