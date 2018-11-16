package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import java.util.*;

@Component("fstDbNumCache")
public class FstDbNumCache extends AbstractCache<Integer, FstDbNum>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<String, Integer> beforeAfterRewardMap;
    private int maxNum;
    private int canGainMaxScore;
    
    public FstDbNumCache() {
        this.beforeAfterRewardMap = new ConcurrentHashMap<String, Integer>();
        this.maxNum = 0;
        this.canGainMaxScore = 0;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<FstDbNum> list = this.dataLoader.getModels((Class)FstDbNum.class);
        for (final FstDbNum temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getCityOcc() > this.maxNum) {
                this.maxNum = temp.getCityOcc();
            }
            this.canGainMaxScore += temp.getCityOcc();
        }
    }
    
    public int getCanGainMaxScore() {
        return this.canGainMaxScore;
    }
    
    public int getReward(final int before, final int after) {
        final String key = this.getKey(before, after);
        if (this.beforeAfterRewardMap.containsKey(key)) {
            return this.beforeAfterRewardMap.get(key);
        }
        final int size = this.getModels().size();
        int beforeReward = 0;
        for (int i = 1; i <= size; ++i) {
            final FstDbNum fdn = (FstDbNum)this.get((Object)i);
            if (before < fdn.getCityOcc()) {
                break;
            }
            beforeReward += fdn.getDNum();
        }
        int afterReward = 0;
        for (int j = 1; j <= size; ++j) {
            final FstDbNum fdn2 = (FstDbNum)this.get((Object)j);
            if (after < fdn2.getCityOcc()) {
                break;
            }
            afterReward += fdn2.getDNum();
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
