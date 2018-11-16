package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdTributeResult
{
    int seasonId;
    List<KfwdTributeRuleInfo> ruleList;
    private List<KfwdRankingRewardInfo> rankingRewardList;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public List<KfwdTributeRuleInfo> getRuleList() {
        return this.ruleList;
    }
    
    public void setRuleList(final List<KfwdTributeRuleInfo> ruleList) {
        this.ruleList = ruleList;
    }
    
    public void setRankingRewardList(final List<KfwdRankingRewardInfo> rankingRewardList) {
        this.rankingRewardList = rankingRewardList;
    }
    
    public List<KfwdRankingRewardInfo> getRankingRewardList() {
        return this.rankingRewardList;
    }
}
