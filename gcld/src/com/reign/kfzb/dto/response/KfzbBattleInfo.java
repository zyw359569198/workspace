package com.reign.kfzb.dto.response;

import com.reign.kfzb.dto.request.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import java.util.*;
import com.reign.kfzb.constants.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfzbBattleInfo
{
    int competitorId;
    int matchId;
    int round;
    int layerRound;
    long battleCd;
    long battleId;
    KfzbPlayerInfo p1;
    KfzbPlayerInfo p2;
    boolean p1KickedOut;
    boolean p2KickedOut;
    int p1Win;
    int p2Win;
    int sup1;
    int sup2;
    KfwdGInfo g1;
    KfwdGInfo g2;
    long nextCd;
    int terrain;
    int winTicket;
    int battleRes;
    Date battleTime;
    boolean needChange;
    
    @JsonIgnore
    public int[] getP1WinRes() {
        return KfzbCommonConstants.getBattleResByRes(this.battleRes, this.layerRound);
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    @JsonIgnore
    public boolean isFinish() {
        return this.p1Win + this.p2Win == this.layerRound;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public KfzbPlayerInfo getP1() {
        return this.p1;
    }
    
    public void setP1(final KfzbPlayerInfo p1) {
        this.p1 = p1;
    }
    
    public KfzbPlayerInfo getP2() {
        return this.p2;
    }
    
    public void setP2(final KfzbPlayerInfo p2) {
        this.p2 = p2;
    }
    
    public int getP1Win() {
        return this.p1Win;
    }
    
    public void setP1Win(final int p1Win) {
        this.p1Win = p1Win;
    }
    
    public int getP2Win() {
        return this.p2Win;
    }
    
    public void setP2Win(final int p2Win) {
        this.p2Win = p2Win;
    }
    
    public int getSup1() {
        return this.sup1;
    }
    
    public void setSup1(final int sup1) {
        this.sup1 = sup1;
    }
    
    public int getSup2() {
        return this.sup2;
    }
    
    public void setSup2(final int sup2) {
        this.sup2 = sup2;
    }
    
    public KfwdGInfo getG1() {
        return this.g1;
    }
    
    public void setG1(final KfwdGInfo g1) {
        this.g1 = g1;
    }
    
    public KfwdGInfo getG2() {
        return this.g2;
    }
    
    public void setG2(final KfwdGInfo g2) {
        this.g2 = g2;
    }
    
    public long getNextCd() {
        return this.nextCd;
    }
    
    public void setNextCd(final long nextCd) {
        this.nextCd = nextCd;
    }
    
    public boolean getP1KickedOut() {
        return this.p1KickedOut;
    }
    
    public void setP1KickedOut(final boolean p1KickedOut) {
        this.p1KickedOut = p1KickedOut;
    }
    
    public boolean getP2KickedOut() {
        return this.p2KickedOut;
    }
    
    public void setP2KickedOut(final boolean p2KickedOut) {
        this.p2KickedOut = p2KickedOut;
    }
    
    public int getLayerRound() {
        return this.layerRound;
    }
    
    public void setLayerRound(final int layerRound) {
        this.layerRound = layerRound;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final int terrain) {
        this.terrain = terrain;
    }
    
    public int getWinTicket() {
        return this.winTicket;
    }
    
    public void setWinTicket(final int winTicket) {
        this.winTicket = winTicket;
    }
    
    public long getBattleCd() {
        return this.battleCd;
    }
    
    public void setBattleCd(final long battleCd) {
        this.battleCd = battleCd;
    }
    
    public long getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final long battleId) {
        this.battleId = battleId;
    }
    
    public Date getBattleTime() {
        return this.battleTime;
    }
    
    public void setBattleTime(final Date battleTime) {
        this.battleTime = battleTime;
    }
    
    public boolean isNeedChange() {
        return this.needChange;
    }
    
    public void setNeedChange(final boolean needChange) {
        this.needChange = needChange;
    }
    
    public int getBattleRes() {
        return this.battleRes;
    }
    
    public void setBattleRes(final int battleRes) {
        this.battleRes = battleRes;
    }
}
