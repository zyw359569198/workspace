package com.reign.kf.comm.entity.match;

import java.util.*;

public class MatchRTInfoEntity
{
    private MatchPlayerEntity player1;
    private MatchPlayerEntity player2;
    private int matchId;
    private int season;
    private int session;
    private int turn;
    private int player1WinNum;
    private int player2WinNum;
    private int matchNum;
    private long matchCD;
    private Date matchTime;
    private int version;
    
    public MatchPlayerEntity getPlayer1() {
        return this.player1;
    }
    
    public void setPlayer1(final MatchPlayerEntity player1) {
        this.player1 = player1;
    }
    
    public MatchPlayerEntity getPlayer2() {
        return this.player2;
    }
    
    public void setPlayer2(final MatchPlayerEntity player2) {
        this.player2 = player2;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getSeason() {
        return this.season;
    }
    
    public void setSeason(final int season) {
        this.season = season;
    }
    
    public int getSession() {
        return this.session;
    }
    
    public void setSession(final int session) {
        this.session = session;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public void setTurn(final int turn) {
        this.turn = turn;
    }
    
    public int getPlayer1WinNum() {
        return this.player1WinNum;
    }
    
    public void setPlayer1WinNum(final int player1WinNum) {
        this.player1WinNum = player1WinNum;
    }
    
    public int getPlayer2WinNum() {
        return this.player2WinNum;
    }
    
    public void setPlayer2WinNum(final int player2WinNum) {
        this.player2WinNum = player2WinNum;
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
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public Date getMatchTime() {
        return this.matchTime;
    }
    
    public void setMatchTime(final Date matchTime) {
        this.matchTime = matchTime;
    }
    
    public void setMatchCd() {
        this.matchCD = this.getMatchTime().getTime() - System.currentTimeMillis();
        this.matchCD = ((this.matchCD < 0L) ? 0L : this.matchCD);
    }
}
