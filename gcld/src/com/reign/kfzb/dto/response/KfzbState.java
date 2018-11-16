package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbState
{
    private int seasonId;
    private int globalState;
    private long nextGlobalStateCD;
    int layer;
    int totalLayer;
    KfzbPhase2Info phase2Info;
    int round;
    int nextLayer;
    int nextRound;
    int layerRound;
    Date battleTime;
    Date nextBatTime;
    Date nextRBegTime;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getGlobalState() {
        return this.globalState;
    }
    
    public void setGlobalState(final int globalState) {
        this.globalState = globalState;
    }
    
    public long getNextGlobalStateCD() {
        return this.nextGlobalStateCD;
    }
    
    public void setNextGlobalStateCD(final long nextGlobalStateCD) {
        this.nextGlobalStateCD = nextGlobalStateCD;
    }
    
    public int getLayer() {
        return this.layer;
    }
    
    public void setLayer(final int layer) {
        this.layer = layer;
    }
    
    public KfzbPhase2Info getPhase2Info() {
        return this.phase2Info;
    }
    
    public void setPhase2Info(final KfzbPhase2Info phase2Info) {
        this.phase2Info = phase2Info;
    }
    
    public void setCurrentTimestamp(final long currentTimeMillis) {
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getNextLayer() {
        return this.nextLayer;
    }
    
    public void setNextLayer(final int nextLayer) {
        this.nextLayer = nextLayer;
    }
    
    public int getNextRound() {
        return this.nextRound;
    }
    
    public void setNextRound(final int nextRound) {
        this.nextRound = nextRound;
    }
    
    public int getLayerRound() {
        return this.layerRound;
    }
    
    public void setLayerRound(final int layerRound) {
        this.layerRound = layerRound;
    }
    
    public Date getBattleTime() {
        return this.battleTime;
    }
    
    public void setBattleTime(final Date battleTime) {
        this.battleTime = battleTime;
    }
    
    public Date getNextBatTime() {
        return this.nextBatTime;
    }
    
    public void setNextBatTime(final Date nextBatTime) {
        this.nextBatTime = nextBatTime;
    }
    
    public Date getNextRBegTime() {
        return this.nextRBegTime;
    }
    
    public void setNextRBegTime(final Date nextRBegTime) {
        this.nextRBegTime = nextRBegTime;
    }
    
    public int getTotalLayer() {
        return this.totalLayer;
    }
    
    public void setTotalLayer(final int totalLayer) {
        this.totalLayer = totalLayer;
    }
}
