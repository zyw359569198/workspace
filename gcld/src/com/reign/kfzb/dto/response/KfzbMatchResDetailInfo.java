package com.reign.kfzb.dto.response;

public class KfzbMatchResDetailInfo
{
    int p1Id;
    int p2Id;
    int seasonId;
    int matchId;
    int round;
    String p1Name;
    String p2Name;
    int p1Level;
    int p2Level;
    String report;
    int winnerId;
    int readNum;
    int dayRead;
    
    public int getP1Id() {
        return this.p1Id;
    }
    
    public void setP1Id(final int p1Id) {
        this.p1Id = p1Id;
    }
    
    public int getP2Id() {
        return this.p2Id;
    }
    
    public void setP2Id(final int p2Id) {
        this.p2Id = p2Id;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public String getP1Name() {
        return this.p1Name;
    }
    
    public void setP1Name(final String p1Name) {
        this.p1Name = p1Name;
    }
    
    public String getP2Name() {
        return this.p2Name;
    }
    
    public void setP2Name(final String p2Name) {
        this.p2Name = p2Name;
    }
    
    public int getP1Level() {
        return this.p1Level;
    }
    
    public void setP1Level(final int p1Level) {
        this.p1Level = p1Level;
    }
    
    public int getP2Level() {
        return this.p2Level;
    }
    
    public void setP2Level(final int p2Level) {
        this.p2Level = p2Level;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
    
    public int getWinnerId() {
        return this.winnerId;
    }
    
    public void setWinnerId(final int winnerId) {
        this.winnerId = winnerId;
    }
    
    public int getReadNum() {
        return this.readNum;
    }
    
    public void setReadNum(final int readNum) {
        this.readNum = readNum;
    }
    
    public int getDayRead() {
        return this.dayRead;
    }
    
    public void setDayRead(final int dayRead) {
        this.dayRead = dayRead;
    }
}
