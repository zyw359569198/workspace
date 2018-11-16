package com.reign.gcld.duel.model;

public class Record
{
    private int playerId;
    private boolean isAtt;
    private boolean isWin;
    private int score;
    
    public Record(final int playerId, final boolean isAtt, final boolean isWin, final int score) {
        this.playerId = playerId;
        this.isAtt = isAtt;
        this.isWin = isWin;
        this.score = score;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public void setIsAtt(final boolean isAtt) {
        this.isAtt = isAtt;
    }
    
    public boolean getIsAtt() {
        return this.isAtt;
    }
    
    public boolean isWin() {
        return this.isWin;
    }
    
    public void setWin(final boolean isWin) {
        this.isWin = isWin;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
}
