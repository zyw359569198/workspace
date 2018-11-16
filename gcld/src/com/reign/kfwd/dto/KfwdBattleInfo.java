package com.reign.kfwd.dto;

import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kf.comm.entity.kfwd.response.*;

public class KfwdBattleInfo
{
    private static final long serialVersionUID = 1L;
    private int matchId;
    private long battleId;
    private int competitorId;
    private int competitorId1;
    private int competitorId2;
    private int inspire1;
    private int inspire2;
    private int[] winRes;
    private int round;
    private int sRound;
    private long nextSRoundCD;
    private int score;
    private int lastScore;
    private int ticket;
    private int lastTicket;
    private KfwdPlayerInfo p1Info;
    private KfwdPlayerInfo p2Info;
    private KfwdGInfo p1gInfo;
    private KfwdGInfo p2gInfo;
    private int doubleCoef;
    private long battleCD;
    int terrain;
    private int doubleCost;
    
    public int getDoubleCost() {
        return this.doubleCost;
    }
    
    public void setDoubleCost(final int doubleCost) {
        this.doubleCost = doubleCost;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getCompetitorId1() {
        return this.competitorId1;
    }
    
    public void setCompetitorId1(final int competitorId1) {
        this.competitorId1 = competitorId1;
    }
    
    public int getCompetitorId2() {
        return this.competitorId2;
    }
    
    public void setCompetitorId2(final int competitorId2) {
        this.competitorId2 = competitorId2;
    }
    
    public int getInspire1() {
        return this.inspire1;
    }
    
    public void setInspire1(final int inspire1) {
        this.inspire1 = inspire1;
    }
    
    public int getInspire2() {
        return this.inspire2;
    }
    
    public void setInspire2(final int inspire2) {
        this.inspire2 = inspire2;
    }
    
    public int[] getWinRes() {
        return this.winRes;
    }
    
    public void setWinRes(final int[] winRes) {
        this.winRes = winRes;
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
    
    public long getNextSRoundCD() {
        return this.nextSRoundCD;
    }
    
    public void setNextSRoundCD(final long nextSRoundCD) {
        this.nextSRoundCD = nextSRoundCD;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getLastScore() {
        return this.lastScore;
    }
    
    public void setLastScore(final int lastScore) {
        this.lastScore = lastScore;
    }
    
    public int getTicket() {
        return this.ticket;
    }
    
    public void setTicket(final int ticket) {
        this.ticket = ticket;
    }
    
    public int getLastTicket() {
        return this.lastTicket;
    }
    
    public void setLastTicket(final int lastTicket) {
        this.lastTicket = lastTicket;
    }
    
    public KfwdPlayerInfo getP1Info() {
        return this.p1Info;
    }
    
    public void setP1Info(final KfwdPlayerInfo p1Info) {
        this.p1Info = p1Info;
    }
    
    public KfwdPlayerInfo getP2Info() {
        return this.p2Info;
    }
    
    public void setP2Info(final KfwdPlayerInfo p2Info) {
        this.p2Info = p2Info;
    }
    
    public KfwdGInfo getP1gInfo() {
        return this.p1gInfo;
    }
    
    public void setP1gInfo(final KfwdGInfo p1gInfo) {
        this.p1gInfo = p1gInfo;
    }
    
    public KfwdGInfo getP2gInfo() {
        return this.p2gInfo;
    }
    
    public void setP2gInfo(final KfwdGInfo p2gInfo) {
        this.p2gInfo = p2gInfo;
    }
    
    public int getDoubleCoef() {
        return this.doubleCoef;
    }
    
    public void setDoubleCoef(final int doubleCoef) {
        this.doubleCoef = doubleCoef;
    }
    
    public long getBattleCD() {
        return this.battleCD;
    }
    
    public void setBattleCD(final long battleCD) {
        this.battleCD = battleCD;
    }
    
    public long getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final long battleId) {
        this.battleId = battleId;
    }
    
    public int getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final int terrain) {
        this.terrain = terrain;
    }
}
