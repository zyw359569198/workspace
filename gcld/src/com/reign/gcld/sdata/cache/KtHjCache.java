package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.util.*;

@Component("ktHjCache")
public class KtHjCache extends AbstractCache<Integer, KtHjR>
{
    private Map<Integer, Integer> toIdMap;
    @Autowired
    private SDataLoader dataLoader;
    
    public KtHjCache() {
        this.toIdMap = new HashMap<Integer, Integer>();
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<KtHjR> list = this.dataLoader.getModels((Class)KtHjR.class);
        for (final KtHjR r : list) {
            super.put((Object)r.getId(), (Object)r);
            this.toIdMap.put(r.getKindomLv() * 100 + r.getPeriod() * 10 + r.getCityNum(), r.getId());
        }
    }
    
    public Tuple<Integer, Integer> getReward(final int forceLv, final int period, final int cityNum) {
        final Integer id = this.toIdMap.get(forceLv * 100 + period * 10 + cityNum);
        if (id == null) {
            return null;
        }
        final KtHjR ktHjR = (KtHjR)super.get((Object)id);
        if (ktHjR == null) {
            return null;
        }
        final Tuple<Integer, Integer> result = new Tuple();
        result.left = ktHjR.getRewardExp();
        result.right = ktHjR.getRewardIron();
        return result;
    }
    
    public Tuple<Integer, Integer> getPeriod2Reward(final int forceLv) {
        final Integer id1 = this.toIdMap.get(forceLv * 100 + 20 + 0);
        final Integer id2 = this.toIdMap.get(forceLv * 100 + 20 + 1);
        if (id1 == null || id2 == null) {
            return null;
        }
        final KtHjR ktHjR1 = (KtHjR)super.get((Object)id1);
        final KtHjR ktHjR2 = (KtHjR)super.get((Object)id2);
        if (ktHjR1 == null || ktHjR2 == null) {
            return null;
        }
        final Tuple<Integer, Integer> result = new Tuple();
        result.left = ktHjR2.getRewardExp() - ktHjR1.getRewardExp();
        result.right = ktHjR2.getRewardIron() - ktHjR1.getRewardIron();
        return result;
    }
    
    @Override
	public void clear() {
        this.toIdMap.clear();
        super.clear();
    }
}
