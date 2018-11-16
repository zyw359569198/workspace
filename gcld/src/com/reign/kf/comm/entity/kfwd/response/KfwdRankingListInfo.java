package com.reign.kf.comm.entity.kfwd.response;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfwdRankingListInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    int scheduleId;
    int totalRound;
    int round;
    int zb;
    int zbWarriorNum;
    List<KfwdRuntimeResultDto> rankingList;
    HashMap<Integer, KfwdRuntimeResultDto> rankingMap;
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public List<KfwdRuntimeResultDto> getRankingList() {
        return this.rankingList;
    }
    
    public void setRankingList(final List<KfwdRuntimeResultDto> rankingList) {
        this.rankingList = rankingList;
    }
    
    public int getTotalRound() {
        return this.totalRound;
    }
    
    public void setTotalRound(final int totalRound) {
        this.totalRound = totalRound;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getZb() {
        return this.zb;
    }
    
    public void setZb(final int zb) {
        this.zb = zb;
    }
    
    public int getZbWarriorNum() {
        return this.zbWarriorNum;
    }
    
    public void setZbWarriorNum(final int zbWarriorNum) {
        this.zbWarriorNum = zbWarriorNum;
    }
    
    @JsonIgnore
    public HashMap<Integer, KfwdRuntimeResultDto> getRankingMap() {
        return this.rankingMap;
    }
    
    public void setRankingMap(final HashMap<Integer, KfwdRuntimeResultDto> rankingMap) {
        this.rankingMap = rankingMap;
    }
}
