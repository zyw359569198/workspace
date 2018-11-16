package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.common.util.*;

@Component("hourlyRewardCache")
public class HourlyRewardCache extends AbstractCache<Integer, HourlyReward>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Double, Integer> rateMap;
    
    public HourlyRewardCache() {
        this.rateMap = new HashMap<Double, Integer>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<HourlyReward> resultList = this.dataLoader.getModels((Class)HourlyReward.class);
        for (final HourlyReward temp : resultList) {
            super.put((Object)temp.getId(), (Object)temp);
            this.rateMap.put(temp.getProb(), temp.getId());
        }
    }
    
    public HourlyReward getHourlyReward() {
        double rate = WebUtil.nextDouble();
        for (final Double key : this.rateMap.keySet()) {
            if (rate <= key) {
                return (HourlyReward)this.get((Object)this.rateMap.get(key));
            }
            rate -= key;
        }
        return (HourlyReward)this.get((Object)1);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.rateMap.clear();
    }
}
