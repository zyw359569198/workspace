package com.reign.kf.comm.entity.match;

public class MatchScheduleEntity
{
    private int matchId;
    private int player1;
    private int player2;
    private int turn;
    private int matchNum;
    private long matchCD;
    private int session;
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getPlayer1() {
        return this.player1;
    }
    
    public void setPlayer1(final int player1) {
        this.player1 = player1;
    }
    
    public int getPlayer2() {
        return this.player2;
    }
    
    public void setPlayer2(final int player2) {
        this.player2 = player2;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public void setTurn(final int turn) {
        this.turn = turn;
    }
    
    public int getMatchNum() {
        return this.matchNum;
    }
    
    public void setMatchNum(final int matchNum) {
        this.matchNum = matchNum;
    }
    
    public long getMatchCD() {
        return this.matchCD;
    }
    
    public void setMatchCD(final long matchCD) {
        this.matchCD = matchCD;
    }
    
    public int getSession() {
        return this.session;
    }
    
    public void setSession(final int session) {
        this.session = session;
    }
}
