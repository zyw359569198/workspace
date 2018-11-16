package com.reign.kfwd.battle;

import com.reign.framework.json.*;

public class KfwdBattleRes
{
    int player1Id;
    int player2Id;
    boolean isP1Win;
    int p1Ticket;
    int p2Ticket;
    int p1Score;
    int p2Score;
    int p1Lost;
    int p2Lost;
    int p1TotalScore;
    int p2TotalScore;
    
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
    
    public int getP1Ticket() {
        return this.p1Ticket;
    }
    
    public void setP1Ticket(final int p1Ticket) {
        this.p1Ticket = p1Ticket;
    }
    
    public int getP2Ticket() {
        return this.p2Ticket;
    }
    
    public void setP2Ticket(final int p2Ticket) {
        this.p2Ticket = p2Ticket;
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
    
    public int getP1Lost() {
        return this.p1Lost;
    }
    
    public void setP1Lost(final int p1Lost) {
        this.p1Lost = p1Lost;
    }
    
    public int getP2Lost() {
        return this.p2Lost;
    }
    
    public void setP2Lost(final int p2Lost) {
        this.p2Lost = p2Lost;
    }
    
    public int getP1TotalScore() {
        return this.p1TotalScore;
    }
    
    public void setP1TotalScore(final int p1TotalScore) {
        this.p1TotalScore = p1TotalScore;
    }
    
    public int getP2TotalScore() {
        return this.p2TotalScore;
    }
    
    public void setP2TotalScore(final int p2TotalScore) {
        this.p2TotalScore = p2TotalScore;
    }
    
    public boolean isP1Win() {
        return this.isP1Win;
    }
    
    public void setP1Win(final boolean isP1Win) {
        this.isP1Win = isP1Win;
    }
    
    public byte[] getResJson(final int competitorId, final long nextRoundCd) {
        if (competitorId == this.player1Id) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("winState", this.isP1Win ? 1 : 2);
            doc.createElement("score", this.p1Score);
            doc.createElement("TotalScore", this.p1TotalScore);
            doc.createElement("ticket", this.p1Ticket);
            doc.createElement("lost", this.p1Lost);
            doc.createElement("kill", this.p2Lost);
            doc.createElement("nextRoundCd", nextRoundCd);
            doc.endObject();
            return doc.toByte();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("winState", this.isP1Win ? 2 : 1);
        doc.createElement("score", this.p2Score);
        doc.createElement("TotalScore", this.p2TotalScore);
        doc.createElement("ticket", this.p2Ticket);
        doc.createElement("lost", this.p2Lost);
        doc.createElement("kill", this.p1Lost);
        doc.createElement("nextRoundCd", nextRoundCd);
        doc.endObject();
        return doc.toByte();
    }
    
    public StringBuilder getResReport7(final int competitorId, final long nextRoundCd) {
        final StringBuilder sb = new StringBuilder();
        if (competitorId == this.player1Id) {
            sb.append(7).append("|").append(this.isP1Win ? 1 : 2).append("|").append(this.p1Score).append("|").append(this.p1TotalScore).append("|").append(this.p1Ticket).append("|").append(this.p2Lost).append("|").append(this.p1Lost).append("|").append(nextRoundCd).append("#");
            return sb;
        }
        sb.append(7).append("|").append(this.isP1Win ? 2 : 1).append("|").append(this.p2Score).append("|").append(this.p2TotalScore).append("|").append(this.p2Ticket).append("|").append(this.p1Lost).append("|").append(this.p2Lost).append("|").append(nextRoundCd).append("#");
        return sb;
    }
    
    public static void main(final String[] args) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("winState", 1);
        doc.createElement("score", 2);
        doc.createElement("TotalScore", 1);
        doc.createElement("ticket", 33);
        doc.endObject();
        System.out.println(new String(JsonBuilder.getJson(State.SUCCESS, "1", doc.toByte())));
    }
}
