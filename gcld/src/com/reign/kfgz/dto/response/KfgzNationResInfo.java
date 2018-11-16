package com.reign.kfgz.dto.response;

import java.util.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfgzNationResInfo
{
    public static final int STATE_SUC = 1;
    public static final int STATE_NOTFINISH = 2;
    public static final int STATE_NONE = 3;
    int state;
    int seasonId;
    int gzId;
    int forceId;
    int selfCityCount;
    int oppCityCount;
    boolean isWin;
    int winTicket;
    int cityTicket;
    String serverName1;
    int nation1;
    String serverName2;
    int nation2;
    int soloRewardCoef;
    int occupyCityRewardCoef;
    String killRankingReward;
    List<KfgzPlayerResultInfo> pList;
    
    public KfgzNationResInfo() {
        this.pList = new ArrayList<KfgzPlayerResultInfo>();
    }
    
    @JsonIgnore
    public int getCityNum1() {
        if (this.forceId == 1) {
            return this.selfCityCount;
        }
        return this.oppCityCount;
    }
    
    @JsonIgnore
    public int getCityNum2() {
        if (this.forceId == 1) {
            return this.oppCityCount;
        }
        return this.selfCityCount;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getSelfCityCount() {
        return this.selfCityCount;
    }
    
    public void setSelfCityCount(final int selfCityCount) {
        this.selfCityCount = selfCityCount;
    }
    
    public int getOppCityCount() {
        return this.oppCityCount;
    }
    
    public void setOppCityCount(final int oppCityCount) {
        this.oppCityCount = oppCityCount;
    }
    
    public boolean isWin() {
        return this.isWin;
    }
    
    public void setWin(final boolean isWin) {
        this.isWin = isWin;
    }
    
    public int getWinTicket() {
        return this.winTicket;
    }
    
    public void setWinTicket(final int winTicket) {
        this.winTicket = winTicket;
    }
    
    public int getCityTicket() {
        return this.cityTicket;
    }
    
    public void setCityTicket(final int cityTicket) {
        this.cityTicket = cityTicket;
    }
    
    public String getServerName1() {
        return this.serverName1;
    }
    
    public void setServerName1(final String serverName1) {
        this.serverName1 = serverName1;
    }
    
    public String getServerName2() {
        return this.serverName2;
    }
    
    public void setServerName2(final String serverName2) {
        this.serverName2 = serverName2;
    }
    
    public int getNation1() {
        return this.nation1;
    }
    
    public void setNation1(final int nation1) {
        this.nation1 = nation1;
    }
    
    public int getNation2() {
        return this.nation2;
    }
    
    public void setNation2(final int nation2) {
        this.nation2 = nation2;
    }
    
    public int getSoloRewardCoef() {
        return this.soloRewardCoef;
    }
    
    public void setSoloRewardCoef(final int soloRewardCoef) {
        this.soloRewardCoef = soloRewardCoef;
    }
    
    public int getOccupyCityRewardCoef() {
        return this.occupyCityRewardCoef;
    }
    
    public void setOccupyCityRewardCoef(final int occupyCityRewardCoef) {
        this.occupyCityRewardCoef = occupyCityRewardCoef;
    }
    
    public List<KfgzPlayerResultInfo> getpList() {
        return this.pList;
    }
    
    public void setpList(final List<KfgzPlayerResultInfo> pList) {
        this.pList = pList;
    }
    
    public String getKillRankingReward() {
        return this.killRankingReward;
    }
    
    public void setKillRankingReward(final String killRankingReward) {
        this.killRankingReward = killRankingReward;
    }
}
