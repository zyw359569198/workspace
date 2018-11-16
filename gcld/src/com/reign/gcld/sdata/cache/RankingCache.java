package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import com.reign.framework.json.*;

@Component("rankingCache")
public class RankingCache extends AbstractCache<Integer, RankingReward>
{
    @Autowired
    private SDataLoader dataLoader;
    int maxCount;
    public List<RankingReward> list1;
    public List<RankingReward> list2;
    
    public RankingCache() {
        this.maxCount = 0;
        this.list1 = new ArrayList<RankingReward>();
        this.list2 = new ArrayList<RankingReward>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.list1.clear();
        this.list2.clear();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<RankingReward> list = this.dataLoader.getModels((Class)RankingReward.class);
        List<RankingReward> temp = null;
        for (final RankingReward reward : list) {
            super.put((Object)reward.getId(), (Object)reward);
            temp = ((reward.getType() == 2) ? this.list2 : this.list1);
            if (!temp.contains(reward)) {
                temp.add(reward);
            }
            if (reward.getCount() > this.maxCount) {
                this.maxCount = reward.getCount();
            }
        }
        Collections.sort(this.list1);
        Collections.sort(this.list2);
    }
    
    public String getRewardIdStr(final int preNum, final int num, final String rewardStr, final int type) {
        if (preNum >= this.maxCount) {
            return rewardStr;
        }
        List<Integer> list = SymbolUtil.stringToList(rewardStr, ",");
        if (list == null) {
            list = new ArrayList<Integer>();
        }
        for (final Integer key : super.getCacheMap().keySet()) {
            final RankingReward reward = (RankingReward)super.get((Object)key);
            if (reward.getType() != type) {
                continue;
            }
            if (list.contains(key)) {
                continue;
            }
            final int count = reward.getCount();
            if (preNum >= count || num < count) {
                continue;
            }
            list.add(reward.getId());
        }
        Collections.sort(list);
        return SymbolUtil.listToString(list, ",");
    }
    
    public List<RankingReward> getRankDivision(final int type) {
        return (type == 2) ? this.list2 : this.list1;
    }
    
    public int checkCanAdd(final Integer playerLv, final int occupyCityNum, final int vTimes) {
        final int nextNum = occupyCityNum + vTimes;
        RankingReward curRankingReward = null;
        for (final RankingReward reward : this.list2) {
            if (playerLv < reward.getLv()) {
                break;
            }
            curRankingReward = reward;
        }
        if (occupyCityNum >= curRankingReward.getCount()) {
            return 0;
        }
        if (nextNum > curRankingReward.getCount()) {
            return curRankingReward.getCount() - occupyCityNum;
        }
        return vTimes;
    }
    
    public void checkIsFull(final int playerLv, final int value, final JsonDocument doc) {
        RankingReward curRankingReward = null;
        for (final RankingReward reward : this.list2) {
            if (playerLv < reward.getLv()) {
                break;
            }
            curRankingReward = reward;
        }
        if (curRankingReward != null && value >= curRankingReward.getCount()) {
            doc.createElement("full", true);
        }
    }
}
