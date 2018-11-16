package com.reign.kfgz.dto.response;

import com.reign.kfgz.dto.request.*;
import java.util.*;

public class KfgzAllRankRes
{
    public static final int STATE_SUC = 1;
    public static final int STATE_UNPREPARE = 2;
    public static final int STATE_NONE = 3;
    public static final int STATE_ONLY_SCHDULEINFO_SUC = 4;
    String gameServer;
    int nation;
    int state;
    int gzId;
    List<KfgzScheduleInfoRes> shList;
    List<KfgzNationResultReq> groupNationRes;
    List<KfgzNationResultReq> layerNationRes;
    List<KfgzPlayerRankingInfoReq> groupKillArmyRes;
    List<KfgzPlayerRankingInfoReq> layerKillArmyRes;
    List<KfgzPlayerRankingInfoReq> groupSoloRes;
    List<KfgzPlayerRankingInfoReq> layerSoloRes;
    List<KfgzPlayerRankingInfoReq> groupOccupyCityRes;
    List<KfgzPlayerRankingInfoReq> layerOccupyCityRes;
    KfgzNationResultReq selfNationRes;
    boolean isLastRound;
    int[] upDownInfo;
    String[] layerNameArray;
    String endRewardString;
    boolean parsed;
    
    public KfgzAllRankRes() {
        this.shList = new ArrayList<KfgzScheduleInfoRes>();
        this.groupNationRes = new ArrayList<KfgzNationResultReq>();
        this.layerNationRes = new ArrayList<KfgzNationResultReq>();
        this.groupKillArmyRes = new ArrayList<KfgzPlayerRankingInfoReq>();
        this.layerKillArmyRes = new ArrayList<KfgzPlayerRankingInfoReq>();
        this.groupSoloRes = new ArrayList<KfgzPlayerRankingInfoReq>();
        this.layerSoloRes = new ArrayList<KfgzPlayerRankingInfoReq>();
        this.groupOccupyCityRes = new ArrayList<KfgzPlayerRankingInfoReq>();
        this.layerOccupyCityRes = new ArrayList<KfgzPlayerRankingInfoReq>();
        this.isLastRound = false;
        this.upDownInfo = new int[2];
        this.parsed = false;
    }
    
    public KfgzNationResultReq safeGetSelfNationRes() {
        if (!this.parsed) {
            this.parseData();
        }
        return this.selfNationRes;
    }
    
    public List<KfgzScheduleInfoRes> safeGetKfgzScheduleInfoResList() {
        if (!this.parsed) {
            this.parseData();
        }
        return this.shList;
    }
    
    public void parseData() {
        final Map<String, Map<Integer, Integer>> rankMap = new HashMap<String, Map<Integer, Integer>>();
        for (final KfgzNationResultReq req : this.groupNationRes) {
            final String gameServer = req.getGameServer();
            final int nation = req.getNation();
            Map<Integer, Integer> map2 = rankMap.get(gameServer);
            if (map2 == null) {
                map2 = new HashMap<Integer, Integer>();
                rankMap.put(gameServer, map2);
            }
            map2.put(nation, req.getPos());
            if (this.gameServer.equals(gameServer) && this.nation == nation) {
                this.selfNationRes = req;
            }
        }
        for (final KfgzScheduleInfoRes sch : this.shList) {
            final String gameServer2 = sch.getGameServer1();
            final int nation2 = sch.getNation1();
            final int pos1 = this.getRank(rankMap, gameServer2, nation2);
            sch.setPos1(pos1);
            final String gameServer3 = sch.getGameServer2();
            final int nation3 = sch.getNation2();
            final int pos2 = this.getRank(rankMap, gameServer3, nation3);
            sch.setPos2(pos2);
        }
    }
    
    private int getRank(final Map<String, Map<Integer, Integer>> rankMap, final String gameServer1, final int nation1) {
        final Map<Integer, Integer> map = rankMap.get(gameServer1);
        if (map == null || map.get(nation1) == null) {
            return 0;
        }
        return map.get(nation1);
    }
    
    public List<KfgzNationResultReq> getGroupNationRes() {
        return this.groupNationRes;
    }
    
    public void setGroupNationRes(final List<KfgzNationResultReq> groupNationRes) {
        this.groupNationRes = groupNationRes;
    }
    
    public List<KfgzNationResultReq> getLayerNationRes() {
        return this.layerNationRes;
    }
    
    public void setLayerNationRes(final List<KfgzNationResultReq> layerNationRes) {
        this.layerNationRes = layerNationRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getGroupKillArmyRes() {
        return this.groupKillArmyRes;
    }
    
    public void setGroupKillArmyRes(final List<KfgzPlayerRankingInfoReq> groupKillArmyRes) {
        this.groupKillArmyRes = groupKillArmyRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getLayerKillArmyRes() {
        return this.layerKillArmyRes;
    }
    
    public void setLayerKillArmyRes(final List<KfgzPlayerRankingInfoReq> layerKillArmyRes) {
        this.layerKillArmyRes = layerKillArmyRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getGroupSoloRes() {
        return this.groupSoloRes;
    }
    
    public void setGroupSoloRes(final List<KfgzPlayerRankingInfoReq> groupSoloRes) {
        this.groupSoloRes = groupSoloRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getLayerSoloRes() {
        return this.layerSoloRes;
    }
    
    public void setLayerSoloRes(final List<KfgzPlayerRankingInfoReq> layerSoloRes) {
        this.layerSoloRes = layerSoloRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getGroupOccupyCityRes() {
        return this.groupOccupyCityRes;
    }
    
    public void setGroupOccupyCityRes(final List<KfgzPlayerRankingInfoReq> groupOccupyCityRes) {
        this.groupOccupyCityRes = groupOccupyCityRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getLayerOccupyCityRes() {
        return this.layerOccupyCityRes;
    }
    
    public void setLayerOccupyCityRes(final List<KfgzPlayerRankingInfoReq> layerOccupyCityRes) {
        this.layerOccupyCityRes = layerOccupyCityRes;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public KfgzNationResultReq getSelfNationRes() {
        return this.selfNationRes;
    }
    
    public void setSelfNationRes(final KfgzNationResultReq selfNationRes) {
        this.selfNationRes = selfNationRes;
    }
    
    public List<KfgzScheduleInfoRes> getShList() {
        return this.shList;
    }
    
    public void setShList(final List<KfgzScheduleInfoRes> shList) {
        this.shList = shList;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public String getEndRewardString() {
        return this.endRewardString;
    }
    
    public void setEndRewardString(final String endRewardString) {
        this.endRewardString = endRewardString;
    }
    
    public boolean isLastRound() {
        return this.isLastRound;
    }
    
    public void setLastRound(final boolean isLastRound) {
        this.isLastRound = isLastRound;
    }
    
    public int[] getUpDownInfo() {
        return this.upDownInfo;
    }
    
    public void setUpDownInfo(final int[] upDownInfo) {
        this.upDownInfo = upDownInfo;
    }
    
    public String[] getLayerNameArray() {
        return this.layerNameArray;
    }
    
    public void setLayerNameArray(final String[] layerNameArray) {
        this.layerNameArray = layerNameArray;
    }
}
