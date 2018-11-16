package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import java.util.*;

@Component("tpFtTnumCache")
public class TpFtTnumCache extends AbstractCache<Integer, TpFtTnum>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, Integer> beforeAfterRateRewardMap;
    private int maxFeat;
    
    public TpFtTnumCache() {
        this.beforeAfterRateRewardMap = new ConcurrentHashMap<String, Integer>();
        this.maxFeat = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TpFtTnum> list = this.dataLoader.getModels((Class)TpFtTnum.class);
        for (final TpFtTnum temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getFeats() > this.maxFeat) {
                this.maxFeat = temp.getFeats();
            }
        }
    }
    
    public int getReward(final int before, final int after, final double rate) {
        final String key = this.getKey(before, after, rate);
        if (this.beforeAfterRateRewardMap.containsKey(key)) {
            return this.beforeAfterRateRewardMap.get(key);
        }
        final int size = this.getModels().size();
        int beforeReward = 0;
        for (int i = 1; i <= size; ++i) {
            final TpFtTnum tct = (TpFtTnum)this.get((Object)i);
            if (before < (int)(tct.getFeats() * rate)) {
                break;
            }
            beforeReward += tct.getTNum();
        }
        int afterReward = 0;
        for (int j = 1; j <= size; ++j) {
            final TpFtTnum tct2 = (TpFtTnum)this.get((Object)j);
            if (after < (int)(tct2.getFeats() * rate)) {
                break;
            }
            afterReward += tct2.getTNum();
        }
        final int reward = afterReward - beforeReward;
        this.beforeAfterRateRewardMap.put(key, reward);
        return reward;
    }
    
    private String getKey(final int before, final int after, final double rate) {
        return String.valueOf(before) + "_" + after + "_" + rate;
    }
    
    public int getMaxFeat(final double rate) {
        return (int)(this.maxFeat * rate);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.beforeAfterRateRewardMap.clear();
        this.maxFeat = 0;
    }
}
