package com.reign.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class KfwdRuntimeMatch implements IModel
{
    private Integer pk;
    private int seasonId;
    private int scheduleId;
    private int player1Id;
    private int player2Id;
    private int player1Win;
    private int player2Win;
    private int p1WinScore;
    private int p2WinScore;
    private int p1Score;
    private int p2Score;
    private int round;
    private int sRound;
    private int sRoundWinner;
    private int winnerId;
    private Date startTime;
    private String reportId;
    private int matchId;
    private int p1pos;
    private int p2pos;
    
    public Integer getPk() {
        return this.pk;
    }
    
    public void setPk(final Integer pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getPlayer1Id() {
        return this.player1Id;
    }
    
    public void setPlayer1Id(final int player1Id) {
        this.player1Id = player1Id;
    }
    
    public int getPlayer2Id() {
        return this.player2Id;
    }
    
    public void setPlayer2Id(final int player2Id) {
        this.player2Id = player2Id;
    }
    
    public int getPlayer1Win() {
        return this.player1Win;
    }
    
    public void setPlayer1Win(final int player1Win) {
        this.player1Win = player1Win;
    }
    
    public int getPlayer2Win() {
        return this.player2Win;
    }
    
    public void setPlayer2Win(final int player2Win) {
        this.player2Win = player2Win;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getsRound() {
        return this.sRound;
    }
    
    public void setsRound(final int sRound) {
        this.sRound = sRound;
    }
    
    public int getsRoundWinner() {
        return this.sRoundWinner;
    }
    
    public void setsRoundWinner(final int sRoundWinner) {
        this.sRoundWinner = sRoundWinner;
    }
    
    public int getWinnerId() {
        return this.winnerId;
    }
    
    public void setWinnerId(final int winnerId) {
        this.winnerId = winnerId;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public String getReportId() {
        return this.reportId;
    }
    
    public void setReportId(final String reportId) {
        this.reportId = reportId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getP1WinScore() {
        return this.p1WinScore;
    }
    
    public void setP1WinScore(final int p1WinScore) {
        this.p1WinScore = p1WinScore;
    }
    
    public int getP2WinScore() {
        return this.p2WinScore;
    }
    
    public void setP2WinScore(final int p2WinScore) {
        this.p2WinScore = p2WinScore;
    }
    
    public int getP1Score() {
        return this.p1Score;
    }
    
    public void setP1Score(final int p1Score) {
        this.p1Score = p1Score;
    }
    
    public int getP2Score() {
        return this.p2Score;
    }
    
    public void setP2Score(final int p2Score) {
        this.p2Score = p2Score;
    }
    
    public int getP1pos() {
        return this.p1pos;
    }
    
    public void setP1pos(final int p1pos) {
        this.p1pos = p1pos;
    }
    
    public int getP2pos() {
        return this.p2pos;
    }
    
    public void setP2pos(final int p2pos) {
        this.p2pos = p2pos;
    }
}
