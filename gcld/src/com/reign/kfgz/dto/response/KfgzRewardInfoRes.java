package com.reign.kfgz.dto.response;

import java.util.*;

public class KfgzRewardInfoRes
{
    Map<Integer, KfgzBattleRewardRes> battleRewardMap;
    Map<Integer, KfgzEndRewardRes> endRewardMap;
    List<KfgzRewardRes> rewardList;
    
    public KfgzRewardInfoRes() {
        this.battleRewardMap = new HashMap<Integer, KfgzBattleRewardRes>();
        this.endRewardMap = new HashMap<Integer, KfgzEndRewardRes>();
        this.rewardList = new ArrayList<KfgzRewardRes>();
    }
    
    public Map<Integer, KfgzBattleRewardRes> getBattleRewardMap() {
        return this.battleRewardMap;
    }
    
    public void setBattleRewardMap(final Map<Integer, KfgzBattleRewardRes> battleRewardMap) {
        this.battleRewardMap = battleRewardMap;
    }
    
    public Map<Integer, KfgzEndRewardRes> getEndRewardMap() {
        return this.endRewardMap;
    }
    
    public void setEndRewardMap(final Map<Integer, KfgzEndRewardRes> endRewardMap) {
        this.endRewardMap = endRewardMap;
    }
    
    public List<KfgzRewardRes> getRewardList() {
        return this.rewardList;
    }
    
    public void setRewardList(final List<KfgzRewardRes> rewardList) {
        this.rewardList = rewardList;
    }
    
    public void getRewardResBySeasonId() {
    }
    
    public KfgzRewardRes getRewardResByLayerIdAndGId(final int layerId, final int rewardgId) {
        for (final KfgzRewardRes res : this.rewardList) {
            if (res.getGroupId() == rewardgId && res.getLayerId() == layerId) {
                return res;
            }
        }
        return null;
    }
}
