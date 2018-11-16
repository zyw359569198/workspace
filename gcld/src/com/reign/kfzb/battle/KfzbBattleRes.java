package com.reign.kfzb.battle;

import com.reign.kfzb.domain.*;
import java.util.*;
import com.reign.framework.json.*;

public class KfzbBattleRes
{
    int player1Id;
    int player2Id;
    boolean isP1Win;
    int p1Ticket;
    int p2Ticket;
    int p1Lost;
    int p2Lost;
    int p1WinNum;
    int p2WinNum;
    
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
            doc.createElement("ticket", this.p1Ticket);
            doc.createElement("lost", this.p1Lost);
            doc.createElement("kill", this.p2Lost);
            doc.createElement("p1Win", this.p1WinNum);
            doc.createElement("p2Win", this.p2WinNum);
            doc.createElement("nextRoundCd", nextRoundCd);
            doc.endObject();
            return doc.toByte();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("winState", this.isP1Win ? 2 : 1);
        doc.createElement("ticket", this.p2Ticket);
        doc.createElement("lost", this.p2Lost);
        doc.createElement("kill", this.p1Lost);
        doc.createElement("p1Win", this.p1WinNum);
        doc.createElement("p2Win", this.p2WinNum);
        doc.createElement("nextRoundCd", nextRoundCd);
        doc.endObject();
        return doc.toByte();
    }
    
    public StringBuilder getResReport7(final int competitorId, final long nextRoundCd, final KfzbRuntimeMatch match) {
        final StringBuilder sb = new StringBuilder();
        final int p1Win = match.getPlayer1Win();
        final int p2Win = match.getPlayer2Win();
        final int pId1 = match.getPlayer1Id();
        final int pId2 = match.getPlayer2Id();
        final Map<Integer, Integer> winMap = new HashMap<Integer, Integer>();
        winMap.put(pId1, p1Win);
        winMap.put(pId2, p2Win);
        int getTickets = 0;
        if (competitorId == this.player1Id) {
            getTickets = this.p1Ticket;
        }
        else if (competitorId == this.player2Id) {
            getTickets = this.p2Ticket;
        }
        if (competitorId == this.player1Id) {
            sb.append(7).append("|").append(this.isP1Win ? 1 : 2).append("|").append(this.p2Lost).append("|").append(this.p1Lost).append("|").append(nextRoundCd).append("|").append(winMap.get(this.player1Id)).append("|").append(winMap.get(this.player2Id)).append("|").append(getTickets).append("|").append("#");
            return sb;
        }
        if (competitorId == this.player2Id) {
            sb.append(7).append("|").append(this.isP1Win ? 2 : 1).append("|").append(this.p1Lost).append("|").append(this.p2Lost).append("|").append(nextRoundCd).append("|").append(winMap.get(this.player1Id)).append("|").append(winMap.get(this.player2Id)).append("|").append(getTickets).append("|").append("#");
            return sb;
        }
        sb.append(7).append("|").append(this.isP1Win ? 1 : 2).append("|").append(this.p2Lost).append("|").append(this.p1Lost).append("|").append(nextRoundCd).append("|").append(winMap.get(this.player1Id)).append("|").append(winMap.get(this.player2Id)).append("|").append("#");
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
    
    public int getP1WinNum() {
        return this.p1WinNum;
    }
    
    public void setP1WinNum(final int p1WinNum) {
        this.p1WinNum = p1WinNum;
    }
    
    public int getP2WinNum() {
        return this.p2WinNum;
    }
    
    public void setP2WinNum(final int p2WinNum) {
        this.p2WinNum = p2WinNum;
    }
}
