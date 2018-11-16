package com.reign.kf.match.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;
import java.util.*;

@JdbcEntity
public class GcldMatch implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    @AutoGenerator
    private int id;
    private int player1;
    private int player2;
    private int matchSeason;
    private int turn;
    private int matchSession;
    private int matchNum;
    private int winner;
    private int finalWinner;
    private int player1Winnum;
    private int player2Winnum;
    private Date scheduleTime;
    private Date matchTime;
    private String matchTag;
    private int player1Inspire;
    private int player2Inspire;
    
    public int getPlayer1Inspire() {
        return this.player1Inspire;
    }
    
    public void setPlayer1Inspire(final int player1Inspire) {
        this.player1Inspire = player1Inspire;
    }
    
    public int getPlayer2Inspire() {
        return this.player2Inspire;
    }
    
    public void setPlayer2Inspire(final int player2Inspire) {
        this.player2Inspire = player2Inspire;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
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
    
    public int getMatchSession() {
        return this.matchSession;
    }
    
    public void setMatchSession(final int matchSession) {
        this.matchSession = matchSession;
    }
    
    public int getMatchNum() {
        return this.matchNum;
    }
    
    public void setMatchNum(final int matchNum) {
        this.matchNum = matchNum;
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
    
    public Date getScheduleTime() {
        return this.scheduleTime;
    }
    
    public void setScheduleTime(final Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Date getMatchTime() {
        return this.matchTime;
    }
    
    public void setMatchTime(final Date matchTime) {
        this.matchTime = matchTime;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
}
