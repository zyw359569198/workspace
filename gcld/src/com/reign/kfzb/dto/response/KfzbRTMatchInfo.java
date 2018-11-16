package com.reign.kfzb.dto.response;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.annotate.*;
import com.reign.kfzb.constants.*;

@JsonAutoDetect
public class KfzbRTMatchInfo implements Serializable
{
    public static final int STATE_LOST = 2;
    private static final long serialVersionUID = 1L;
    private int cId;
    private int matchId;
    private int cId1;
    private int showMatchId;
    private int cId2;
    private int showcId1;
    private int showcId2;
    private int lastcId1;
    private int lastcId2;
    private int res;
    private int lastRes;
    private int layer;
    private int showlayer;
    private int round;
    private int showRound;
    private int state;
    private long nextRoundCD;
    private long nextShowRoundCD;
    private String lastReport;
    private Date roundBattleTime;
    private Date lastRoundBattleTime;
    private int lastMatchId;
    private int showLayerRound;
    private long lastSynTime;
    
    @JsonIgnore
    public int isLastRoundFinish() {
        if (this.showlayer > 1) {
            return 0;
        }
        if (this.showRound < this.showLayerRound) {
            return 0;
        }
        if (this.nextShowRoundCD == 0L) {
            return 1;
        }
        return 0;
    }
    
    @JsonIgnore
    public int getRoundWinner() {
        return 0;
    }
    
    public int getShowLayerRound() {
        return this.showLayerRound;
    }
    
    public void setShowLayerRound(final int showLayerRound) {
        this.showLayerRound = showLayerRound;
    }
    
    public int getLastMatchId() {
        return this.lastMatchId;
    }
    
    public void setLastMatchId(final int lastMatchId) {
        this.lastMatchId = lastMatchId;
    }
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getcId1() {
        return this.cId1;
    }
    
    public void setcId1(final int cId1) {
        this.cId1 = cId1;
    }
    
    public int getcId2() {
        return this.cId2;
    }
    
    public void setcId2(final int cId2) {
        this.cId2 = cId2;
    }
    
    public int getLastcId1() {
        return this.lastcId1;
    }
    
    public void setLastcId1(final int lastcId1) {
        this.lastcId1 = lastcId1;
    }
    
    public int getLastcId2() {
        return this.lastcId2;
    }
    
    public void setLastcId2(final int lastcId2) {
        this.lastcId2 = lastcId2;
    }
    
    public int getRes() {
        return this.res;
    }
    
    public void setRes(final int res) {
        this.res = res;
    }
    
    public int getLastRes() {
        return this.lastRes;
    }
    
    public void setLastRes(final int lastRes) {
        this.lastRes = lastRes;
    }
    
    public int getLayer() {
        return this.layer;
    }
    
    public void setLayer(final int layer) {
        this.layer = layer;
    }
    
    public int getShowlayer() {
        return this.showlayer;
    }
    
    public void setShowlayer(final int showlayer) {
        this.showlayer = showlayer;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getShowRound() {
        return this.showRound;
    }
    
    public void setShowRound(final int showRound) {
        this.showRound = showRound;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public long getNextRoundCD() {
        return this.nextRoundCD;
    }
    
    public void setNextRoundCD(final long nextRoundCD) {
        this.nextRoundCD = nextRoundCD;
    }
    
    public long getNextShowRoundCD() {
        return this.nextShowRoundCD;
    }
    
    public void setNextShowRoundCD(final long nextShowRoundCD) {
        this.nextShowRoundCD = nextShowRoundCD;
    }
    
    public String getLastReport() {
        return this.lastReport;
    }
    
    public void setLastReport(final String lastReport) {
        this.lastReport = lastReport;
    }
    
    @JsonIgnore
    public Date getRoundBattleTime() {
        return this.roundBattleTime;
    }
    
    public void setRoundBattleTime(final Date roundBattleTime) {
        this.roundBattleTime = roundBattleTime;
    }
    
    @JsonIgnore
    public Date getLastRoundBattleTime() {
        return this.lastRoundBattleTime;
    }
    
    public void setLastRoundBattleTime(final Date lastRoundBattleTime) {
        this.lastRoundBattleTime = lastRoundBattleTime;
    }
    
    public void addRoundBattleRes(final int round, final int battleRes) {
        this.res = KfzbCommonConstants.addRoundBattleRes(this.res, round, battleRes);
    }
    
    public int getShowMatchId() {
        return this.showMatchId;
    }
    
    public void setShowMatchId(final int showMatchId) {
        this.showMatchId = showMatchId;
    }
    
    public int getShowcId1() {
        return this.showcId1;
    }
    
    public void setShowcId1(final int showcId1) {
        this.showcId1 = showcId1;
    }
    
    public int getShowcId2() {
        return this.showcId2;
    }
    
    public void setShowcId2(final int showcId2) {
        this.showcId2 = showcId2;
    }
    
    public long getLastSynTime() {
        return this.lastSynTime;
    }
    
    public void setLastSynTime(final long lastSynTime) {
        this.lastSynTime = lastSynTime;
    }
    
    public boolean isAttacker(final Integer sCId, final int seasonId, final int layerRound) {
        final boolean p1IsAtt = KfzbCommonConstants.isP1Attack(seasonId, this.layer, this.round, layerRound, this.matchId);
        if (p1IsAtt) {
            return sCId == this.cId1;
        }
        return sCId != this.cId1;
    }
}
