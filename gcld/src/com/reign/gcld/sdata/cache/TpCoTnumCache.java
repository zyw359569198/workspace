package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import java.util.*;

@Component("tpCoTnumCache")
public class TpCoTnumCache extends AbstractCache<Integer, TpCoTnum>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, Integer> beforeAfterRewardMap;
    private int maxNum;
    
    public TpCoTnumCache() {
        this.beforeAfterRewardMap = new ConcurrentHashMap<String, Integer>();
        this.maxNum = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<TpCoTnum> list = this.dataLoader.getModels((Class)TpCoTnum.class);
        for (final TpCoTnum temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getCityOcc() > this.maxNum) {
                this.maxNum = temp.getCityOcc();
            }
        }
    }
    
    public int getReward(final int before, final int after) {
        final String key = this.getKey(before, after);
        if (this.beforeAfterRewardMap.containsKey(key)) {
            return this.beforeAfterRewardMap.get(key);
        }
        final int size = this.getModels().size();
        int beforeReward = 0;
        for (int i = 1; i <= size; ++i) {
            final TpCoTnum tct = (TpCoTnum)this.get((Object)i);
            if (before < tct.getCityOcc()) {
                break;
            }
            beforeReward += tct.getTNum();
        }
        int afterReward = 0;
        for (int j = 1; j <= size; ++j) {
            final TpCoTnum tct2 = (TpCoTnum)this.get((Object)j);
            if (after < tct2.getCityOcc()) {
                break;
            }
            afterReward += tct2.getTNum();
        }
        final int reward = afterReward - beforeReward;
        this.beforeAfterRewardMap.put(key, reward);
        return reward;
    }
    
    private String getKey(final int before, final int after) {
        return String.valueOf(before) + "_" + after;
    }
    
    public int getMaxNum() {
        return this.maxNum;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.beforeAfterRewardMap.clear();
        this.maxNum = 0;
    }
}
