package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdRewardResult
{
    int seasonId;
    private List<KfwdRewardRuleInfo> rewardList;
    private List<KfwdRankingRewardInfo> rankingRewardList;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public List<KfwdRewardRuleInfo> getRewardList() {
        return this.rewardList;
    }
    
    public void setRewardList(final List<KfwdRewardRuleInfo> rewardList) {
        this.rewardList = rewardList;
    }
    
    public List<KfwdRankingRewardInfo> getRankingRewardList() {
        return this.rankingRewardList;
    }
    
    public void setRankingRewardList(final List<KfwdRankingRewardInfo> rankingRewardList) {
        this.rankingRewardList = rankingRewardList;
    }
}
