package com.reign.kf.match.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;
import java.util.*;

@JdbcEntity
public class MatchReport implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    private int matchId;
    private int player1;
    private String playerName1;
    private int player2;
    private String playerName2;
    private int matchSeason;
    private int turn;
    private int winner;
    private int finalWinner;
    private int player1Winnum;
    private int player2Winnum;
    private int matchNum;
    private String matchTag;
    private Date recordTime;
    private int matchSession;
    
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
    
    public String getPlayerName1() {
        return this.playerName1;
    }
    
    public void setPlayerName1(final String playerName1) {
        this.playerName1 = playerName1;
    }
    
    public int getPlayer2() {
        return this.player2;
    }
    
    public void setPlayer2(final int player2) {
        this.player2 = player2;
    }
    
    public String getPlayerName2() {
        return this.playerName2;
    }
    
    public void setPlayerName2(final String playerName2) {
        this.playerName2 = playerName2;
    }
    
    public int getMatchSeason() {
        return this.matchSeason;
    }
    
    public void setMatchSeason(final int matchSeason) {
        this.matchSeason = matchSeason;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public void setTurn(final int turn) {
        this.turn = turn;
    }
    
    public int getWinner() {
        return this.winner;
    }
    
    public void setWinner(final int winner) {
        this.winner = winner;
    }
    
    public int getFinalWinner() {
        return this.finalWinner;
    }
    
    public void setFinalWinner(final int finalWinner) {
        this.finalWinner = finalWinner;
    }
    
    public int getPlayer1Winnum() {
        return this.player1Winnum;
    }
    
    public void setPlayer1Winnum(final int player1Winnum) {
        this.player1Winnum = player1Winnum;
    }
    
    public int getPlayer2Winnum() {
        return this.player2Winnum;
    }
    
    public void setPlayer2Winnum(final int player2Winnum) {
        this.player2Winnum = player2Winnum;
    }
    
    public int getMatchNum() {
        return this.matchNum;
    }
    
    public void setMatchNum(final int matchNum) {
        this.matchNum = matchNum;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public Date getRecordTime() {
        return this.recordTime;
    }
    
    public void setRecordTime(final Date recordTime) {
        this.recordTime = recordTime;
    }
    
    public int getMatchSession() {
        return this.matchSession;
    }
    
    public void setMatchSession(final int matchSession) {
        this.matchSession = matchSession;
    }
}
